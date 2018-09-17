package com.example.mongo;

import com.alibaba.fastjson.JSON;
import com.example.mongo.model.EsQueryResult;
import com.example.mongo.model.EsUser;
import com.example.mongo.utils.EsUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouwei on 2018/9/7
 **/

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class EsTest {

    @Autowired
    private EsUtils esUtils;

    @Test
    public void indexExists() throws IOException {
        String index1 = "test_index";
        String index2 = "user_index";
        boolean flag1 = esUtils.indexExists(index1);
        boolean flag2 = esUtils.indexExists(index2);
        log.info("---------------------------------------index: {} {}; index: {} {}", index1, flag1, index2, flag2);
    }

    @Test
    public void deletedIndex() throws IOException {
        boolean flag = esUtils.deletedIndex("user_index");
        log.info("---------------------------------------deleted index  result: {}", flag);
    }


    @Test
    public void createIndex() throws IOException {
        Map properties = new HashMap();
        properties.put("name", new HashMap() {{put("type", "text"); } });
        properties.put("age", new HashMap() {{put("type", "byte"); } });
        properties.put("sex", new HashMap() {{put("type", "keyword"); } });
        properties.put("birthday", new HashMap() {{
            put("type", "date");
            put("format", "yyyy-MM-dd");
        } });

        boolean result = esUtils.createIndex("user_index", 2, 1, "user", properties);
        log.info("---------------------------------------create index  result: {}", result);
    }


    @Test
    public void putMapping() throws IOException {
        Map properties = new HashMap();
        properties.put("hobby", new HashMap() {{put("type", "text"); } });
        boolean result = esUtils.putMapping("user_index", "user", properties);
        log.info("---------------------------------------putMapping  result: {}", result);
    }

    @Test
    public void add() throws IOException {
        EsUser user = new EsUser("王老五", 18, "男", new Date(), "打太极，葵花宝典");
        IndexResponse resp = esUtils.add("user_index", "user", "4", user);
        log.info("---------------------------------------add  IndexResponse: {}", JSON.toJSONString(resp));
    }

    @Test
    public void batchAdd() throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("2", new EsUser("李四", 17, "男", new Date(), "葵花宝典"));
        data.put("3", new EsUser("王五", 19, "女", new Date(), "降龙十八掌"));

        BulkResponse resp = esUtils.batchAdd("user_index", "user", data);
        log.info("---------------------------------------batchAdd  BulkResponse: {}", JSON.toJSONString(resp));
    }

    @Test
    public void deleteById() throws IOException {
        Boolean  result = esUtils.deleteById("user_index", "user", "4");
        log.info("---------------------------------------delete  result: {}", JSON.toJSONString(result));
    }

    @Test
    public void updateById() throws IOException {
        Map<String, Object> updateMap = new HashMap<String, Object>(){{put("name", "李四六");}};
        Boolean  result = esUtils.updateById("user_index", "user", "2", updateMap);
        log.info("---------------------------------------update  result: {}", JSON.toJSONString(result));
    }

    @Test
    public void getById() throws IOException {
        EsUser user = esUtils.getById("user_index", "user", "1", EsUser.class);
        log.info("---------------------------------------getById  EsUser: {}", JSON.toJSONString(user));
    }

    @Test
    public void query() throws IOException {
        //组合条件
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

        //会对查询条件进行分词处理， 适合text字段
//        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "王五");
//        boolBuilder.must(matchQueryBuilder);

        //不会对查询条件进行分词，适合keyword
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("sex", "男");
        boolBuilder.must(termQueryBuilder);

        //范围查询
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("age").gte(17).lte(19);
        boolBuilder.must(rangeQueryBuilder);

        //查询结果处理
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //查询的列, 默认全查
//        sourceBuilder.fetchSource(new String[]{"name"}, new String[]{});
        //分页
        sourceBuilder.from(0);
        sourceBuilder.size(10);
        //排序
        sourceBuilder.sort("age", SortOrder.DESC);
        sourceBuilder.query(boolBuilder);

        EsQueryResult<EsUser> result = esUtils.query("user_index", "user", sourceBuilder, EsUser.class);
        log.info("---------------------------------------query  result: {}", JSON.toJSONString(result));
    }





}
