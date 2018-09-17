package com.example.mongo.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * Created by zhouwei on 2018/9/14
 **/
public class EsUser {

    private String name;

    private Integer age;

    private String sex;

    @JSONField(format="yyyy-MM-dd")
    private Date birthday;

    private String hobby;

    public EsUser(){}

    public EsUser(String name, Integer age, String sex, Date birthday) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.birthday = birthday;
    }

    public EsUser(String name, Integer age, String sex, Date birthday, String hobby) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.birthday = birthday;
        this.hobby = hobby;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }
}
