package com.example.dao;/**
 * Created by zhouwei03 on 2017/11/24.
 */

import com.example.model.User;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @author zhouwei03
 * @create 2017/11/24
 */
public interface UserRepository extends MongoRepository<User, String> {

    //根据属性查询命名规则： findBy+属性(首字母大写)
    User findByName(String name);

    //根据属性模糊查询命名规则： findBy+属性(首字母大写)+Like
    List<User> findByNameLike(String name);

    @Query(value="{'name':?0}",fields="{'name':1}")
    //value代表查询条件, ？0表示站位符，去方法中的第一个参数;fields指定返回的字段(1返回，-1不返回)，_id默认返回
    Page<User> findByName(String name, Pageable pageable);

    @Query(value="{'name':{'$in':?0}}")
    List<User>findByNames(String[] names);

    @Query(fields="{'name':1}")
    Page<User> findByNameLike(String name, Pageable pageable);

    //大于查询命名规则： findBy+属性(首字母大写)+GreaterThan = query中的value举例：{"age" : {"$gt" : age}}
    List<User> findByAgeGreaterThan(int age);

    //小于查询命名规则： findBy+属性(首字母大写)+LessThan =  query中的value举例：{"age" : {"$lt" : age}}
    List<User> findByAgeLessThan(int age);

    @Query(value="{'id':{'$gte':?0}}")
    //主键处理
    List<User> findByIdThan(ObjectId startId);

    @Query(value="{'creationDate':{'$gt':?0}}")
    //大于指定的时间
    List<User> findByCreationDateThan(Date starDate);

    //之间查询命名规则： findBy+属性(首字母大写)+Between = query中的value举例：{"age" : {"$gt" : min, "$lt" : max}}
    List<User> findByAgeBetween(int min, int max);

    //不包含查询命名规则： findBy+属性(首字母大写)+Not  = query中的value举例：{"name" : {"$ne" : name}}
    List<User> findByNameNot(String name);

    //更新
    WriteResult updateNameById(String id, String name);

}
