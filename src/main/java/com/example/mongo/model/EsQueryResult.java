package com.example.mongo.model;

import java.util.List;

/**
 * ES查询响应结果集
 * Created by zhouwei on 2018/9/17
 **/
public class EsQueryResult<T> {
    //结果总数
    private Long totalCount;

    //分页结果集
    private List<T> dataList;

    public EsQueryResult() {
    }

    public EsQueryResult(Long totalCount, List<T> dataList) {
        this.totalCount = totalCount;
        this.dataList = dataList;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
