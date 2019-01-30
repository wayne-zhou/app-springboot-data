package com.example.model;/**
 * Created by zhouwei03 on 2017/11/24.
 */

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author zhouwei03
 * @create 2017/11/24
 */
@Document(collection="User")  //不加该注解，生成集合名是以小写开头
public class User {

    @Id
    private String id;

    private String name;

    private Integer age;

    @Indexed(name="creationDate_-1", direction= IndexDirection.DESCENDING, background=true)
    private Date creationDate;

    public User() {}

    public User( String name, Integer age, Date creationDate) {
        this.name = name;
        this.age = age;
        this.creationDate = creationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
