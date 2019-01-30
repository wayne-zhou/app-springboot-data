package com.example.utils;

import com.alibaba.fastjson.JSON;
import com.example.model.EsQueryResult;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouwei on 2018/9/7
 **/
@Component
@Slf4j
public class EsUtils {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 索引是否存在
     * @throws IOException
     */
    public boolean indexExists(String index) throws IOException {
        GetIndexRequest req = new GetIndexRequest();
        req.indices(index);
        req.humanReadable(true);
        return restHighLevelClient.indices().exists(req);
    }

    /**
     * 删除索引
     */
    public boolean deletedIndex(String index) throws IOException {
        DeleteIndexRequest req = new DeleteIndexRequest(index);
        DeleteIndexResponse resp = restHighLevelClient.indices().delete(req);
        return resp.isAcknowledged();
    }

    /**
     * 创建索引
     * @param index  必须小写
     * @param shards    分片数
     * @param replicas  副本数
     * @param type
     * @param properties  {name:{type:text}}
     */
    public boolean createIndex(String index, Integer shards, Integer replicas, String type, Map properties) throws  IOException{
        if (indexExists(index)) {
            return true;
        }

        CreateIndexRequest req = new CreateIndexRequest(index);
        //设置分片和副本
        req.settings(Settings.builder().put("number_of_shards", shards).put("number_of_replicas", replicas));
        //设置别名
//        req.alias(new Alias(""));
        //mapping 非必须，ES会创建默认mapping
        if(!CollectionUtils.isEmpty(properties)){
            //方法一
            Map mapping = new HashMap(){{put("properties", properties);}};
            req.mapping(type, new HashMap(){{put(type, mapping);}});

            //方法二
//            req.mapping(type, "{\""+type+"\" : {\"properties\" : "+ JSON.toJSONString(properties)+"}}", XContentType.JSON);

            //方法三
//            XContentBuilder source = XContentFactory.jsonBuilder();
//            builder.startObject()
//                .startObject(type)
//                    .startObject("properties")
//                        .startObject("columnName").field("type", "date").field("format", "yyyy-MM-dd").endObject()
//                    .endObject()
//                .endObject()
//            .endObject();
//            req.mapping(type, source);
        }
        CreateIndexResponse resp =restHighLevelClient.indices().create(req);
        return resp.isAcknowledged();
    }

    /**
     * 添加mapping
     * @param index
     * @param type
     * @param properties  {name:{type:text}}
     * @return
     */
    public boolean putMapping(String index, String type, Map properties) throws  IOException{
        PutMappingRequest req = new PutMappingRequest(index);
        req.type(type).source("{\""+type+"\" : {\"properties\" : "+ JSON.toJSONString(properties)+"}}", XContentType.JSON);
        PutMappingResponse resp =restHighLevelClient.indices().putMapping(req);
        return resp.isAcknowledged();
    }

    /**
     * 添加
     * 存在时会整个覆盖原有文档
     */
    public IndexResponse add(String index, String type, String Id, Object obj) throws IOException {
        IndexRequest req = new IndexRequest(index, type, Id);
        req.source(JSON.toJSONString(obj), XContentType.JSON);
        return restHighLevelClient.index(req);
    }

    /**
     * 批量添加
     */
    public BulkResponse batchAdd(String index, String type, Map<String, Object>data) throws IOException {
        BulkRequest req = new BulkRequest();
        data.forEach((id, obj) -> {
            req.add(new IndexRequest(index, type, id).source(JSON.toJSONString(obj), XContentType.JSON));
        });
        return restHighLevelClient.bulk(req);
    }

    /**
     * 删除
     */
    public boolean deleteById(String index, String type, String id) throws IOException {
        try{
            DeleteRequest req = new DeleteRequest(index, type, id);
            DeleteResponse resp = restHighLevelClient.delete(req);
            if(resp.getResult() == DocWriteResponse.Result.DELETED){
                return true;
            }

            //处理由于文档不存在
            if(resp.getResult() == DocWriteResponse.Result.NOT_FOUND){
                log.info("删除 index:{}, type:{}, id:{} 失败，文档不存在", index, type, id);
            }
        }catch (ElasticsearchException e){
            if (e.status() == RestStatus.CONFLICT) {
                //引发的异常表示返回了版本冲突错误
            }
        }
        return false;
    }

    /**
     * 删除指定ID文档的某个field
     * 如果删除多个field，使用add方法重新覆盖
     */
    public boolean removeFieldById(String index, String type, String id, String fieldName) throws IOException {
        UpdateRequest req = new UpdateRequest(index, type, id);
        String scriptStr = "ctx._source.remove(\""+fieldName+"\")";
        Script updateScript = new Script(ScriptType.INLINE, "painless", scriptStr, new HashMap<>());
        req.script(updateScript);

        try{
            UpdateResponse resp = restHighLevelClient.update(req);
            return resp.getResult() == DocWriteResponse.Result.UPDATED;
        }catch (ElasticsearchException e){
            if (e.status() == RestStatus.NOT_FOUND) {
                //处理由于文档不存在抛出的异常
                log.info("removeField index:{}, type:{}, id:{} , fieldName:{}，文档不存在", index, type, id, fieldName);
                return true;
            }
        }
        return false;
    }


    /**
     * 更新
     * 不存在field时会新增field
     */
    public boolean updateById(String index, String type, String id, Map<String, Object> updateData) throws IOException {
        try{
            UpdateRequest req = new UpdateRequest(index, type, id);
            req.doc(JSON.toJSONString(updateData), XContentType.JSON);
            UpdateResponse resp = restHighLevelClient.update(req);
            return resp.getResult() == DocWriteResponse.Result.UPDATED;
        }catch (ElasticsearchException e){
            if (e.status() == RestStatus.NOT_FOUND) {
                //处理由于文档不存在抛出的异常
                log.info("更新 index:{}, type:{}, id:{} 失败，文档不存在", index, type, id);
            }
        }
        return false;
    }

    /**
     * 带版本更新
     * 相当于乐观锁
     */
    public boolean updateById(String index, String type, String id, Long version, Map<String, Object> updateData) throws IOException {
        try{
            UpdateRequest req = new UpdateRequest(index, type, id);
            req.version(version);
            req.doc(JSON.toJSONString(updateData), XContentType.JSON);
            UpdateResponse resp = restHighLevelClient.update(req);
            return resp.getResult() == DocWriteResponse.Result.UPDATED;
        }catch (ElasticsearchException e){
            if (e.status() == RestStatus.NOT_FOUND) {
                //处理由于文档不存在抛出的异常
                log.info("更新 index:{}, type:{}, id:{} 失败，文档不存在", index, type, id);
            }
            if (e.status() == RestStatus.CONFLICT) {
                //引发的异常表示返回了版本冲突错误
                log.info("更新 index:{}, type:{}, id:{}, version:{} 失败，版本冲突", index, type, id, version, e);;
            }
        }
        return false;
    }

    /**
     * 根据条件批量更新
     * 6.5.x 以上版本支持
     */
    private boolean updateByQuery(String index, String type, SearchSourceBuilder sourceBuilder, Map<String, Object> updateData) throws IOException {
        SearchRequest searchReq = new SearchRequest(index);
        searchReq.types(type);
        searchReq.source(sourceBuilder);

        UpdateByQueryRequest req = new UpdateByQueryRequest(searchReq);
        StringBuffer scriptStr = new StringBuffer();
        updateData.forEach((k,v) -> scriptStr.append("ctx._source.").append(k).append("=params.").append(k).append(";"));
        Script updateScript = new Script(ScriptType.INLINE, "painless", scriptStr.toString(), updateData);
        req.setScript(updateScript);

//        BulkByScrollResponse bulkResponse = restHighLevelClient.updateByQuery(req, RequestOptions.DEFAULT);
        return true;
    }

    /**
     * 根据ID查询
     */
    public <T> T getById(String index, String type, String id, Class<T> clazz) throws IOException {
        GetRequest req = new GetRequest(index, type, id);
        GetResponse resp = restHighLevelClient.get(req);
        if(resp.isExists()){
//            resp.getVersion();
            return JSON.parseObject(resp.getSourceAsString(), clazz);
        }
        return null;
    }

    /**
     * 条件查询
     */
    public <T> EsQueryResult<T> query(String index, String type, SearchSourceBuilder sourceBuilder, Class<T> clazz) throws IOException {
        SearchRequest req = new SearchRequest(index);
        req.types(type);
        req.source(sourceBuilder);
        SearchResponse resp = restHighLevelClient.search(req);
        log.info(JSON.toJSONString(resp));
        SearchHits hists = resp.getHits();
        Long totalCount = hists.getTotalHits(); //符合过滤条件的总数(不受分页影响)
        List<T> list = new ArrayList<T>(hists.getHits().length);
        for (SearchHit hist : hists.getHits()) {
//            hist.getVersion();
            list.add(JSON.parseObject(hist.getSourceAsString(), clazz));
        }
        return new EsQueryResult(totalCount, list);
    }

}
