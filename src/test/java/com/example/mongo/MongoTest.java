package com.example.mongo;/**
 * Created by zhouwei03 on 2017/11/24.
 */

import com.example.mongo.dao.UserRepository;
import com.example.mongo.model.User;
import com.example.mongo.utils.DateUtils;
import com.example.mongo.utils.JsonUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.data.mongodb.core.script.NamedMongoScript;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @author zhouwei03
 * @create 2017/11/24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class MongoTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate template;


    @Test
    public void delete(){
        userRepository.deleteAll();
    }

    @Test
    public void save(){
        userRepository.deleteAll();
        userRepository.save(new User("张三",10, new Date()));
        userRepository.save(new User("李四",20, new Date()));
        userRepository.save(new User("王五",30, new Date()));
        userRepository.save(new User("赵六",40, new Date()));
        Assert.assertEquals(4,userRepository.findAll().size());
    }

    @Test
    public void query(){
        Sort sort1 = new Sort(Sort.Direction.DESC, "id");
        Sort sort2 = new Sort(Sort.Direction.DESC, "id", "age");
        Sort.Order order1 = new Sort.Order(Sort.Direction.ASC, "id");
        Sort.Order order2 = new Sort.Order(Sort.Direction.DESC, "age");
        Sort sort3 = new Sort(order1, order2);

        //排序查询
        List<User> list = userRepository.findAll(sort1);
        System.out.println(JsonUtils.objectToJson(list));

        //分页+排序查询
        Page<User> page1 = userRepository.findAll(new PageRequest(0, 3, sort2));  //mongo分页从0开始
        System.out.println(JsonUtils.objectToJson(page1));
        list= page1.getContent();
        System.out.println(JsonUtils.objectToJson(list));


        Page<User> page2 = userRepository.findByName("李四", new PageRequest(0,10));
        System.out.println("findByName\t"+ JsonUtils.objectToJson(page2));

        list = userRepository.findByNames(new String[]{"李四","王五"});
        System.out.println("findByNames\t"+ JsonUtils.objectToJson(list));

        Page<User> page3 = userRepository.findByNameLike("张", new PageRequest(0,10));
        System.out.println("findByNameLike\t"+JsonUtils.objectToJson(page3));

        list = userRepository.findByAgeGreaterThan(30);
        System.out.println("findByAgeGreaterThan\t"+JsonUtils.objectToJson(list));

        list = userRepository.findByAgeLessThan(20);
        System.out.println("findByAgeLessThan\t"+JsonUtils.objectToJson(list));

        list = userRepository.findByIdThan(new ObjectId("5a30cce9d6f8532e24c359f2"));
        System.out.println("findByIdThan\t"+JsonUtils.objectToJson(list));

        list = userRepository.findByCreationDateThan(DateUtils.parse("2017-12-18","yyyy-MM-dd"));
        System.out.println("findByCreationDateThan\t"+JsonUtils.objectToJson(list));

        list = userRepository.findByAgeBetween(20,40);
        System.out.println("findByAgeBetween\t"+JsonUtils.objectToJson(list));

        list = userRepository.findByNameNot("李四");
        System.out.println("findByNameNot\t"+JsonUtils.objectToJson(list));
    }

    @Test
    public void script(){
        ScriptOperations scriptOps = template.scriptOps();
        //删除js
        template.getCollection("system.js").remove(new BasicDBObject("_id", "testScript"));
        if(!scriptOps.exists("testScript")){
            //注册函数，方便调用
            scriptOps.register(new NamedMongoScript("testScript",
                    "function(name){var ret=db.user.findAndModify({query:{name:name},update:{$inc:{age:1}},new:true});return ret.age}"));
        }
        //调用注册函数
        System.out.println(scriptOps.call("testScript", "张三"));
        DB db = template.getDb();
        System.out.println(db.eval("testScript('张三')"));

        //直接执行函数
        ExecutableMongoScript script = new ExecutableMongoScript("function(x){return x + \".\"}");
        System.out.println(scriptOps.execute(script, 10));

    }

    @Test
    public void updateNameById(){
        WriteResult result = userRepository.updateNameById("5a30cce9d6f8532e24c359f2", "张三1");
        System.out.println(JsonUtils.objectToJson(result));
    }

    @Test
    public void test(){


    }

}
