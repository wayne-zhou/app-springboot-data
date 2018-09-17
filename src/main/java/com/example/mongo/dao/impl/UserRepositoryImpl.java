package com.example.mongo.dao.impl;

import com.example.mongo.model.User;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * Created by zhouwei on 2017/12/18
 **/
@Component
public class UserRepositoryImpl{

    @Autowired
    private MongoTemplate mongoTemplate;

    public WriteResult updateNameById(String id, String name){
        Query query = new Query();
        Criteria criteria = Criteria.where("id").is(id);
//     criteria.and("key").is("")   //多个条件 and
//     criteria.orOperator(Criteria.where("key").is(""))  //
        query.addCriteria(criteria);

        Update update = new Update();
        update.set("name", name);
        //更新第一条
        WriteResult result =  mongoTemplate.updateFirst(query, update, User.class);
        //更新符合条件的所有
//        WriteResult result =  mongoTemplate.updateMulti(query, update, User.class);
        return  result;
    }

}
