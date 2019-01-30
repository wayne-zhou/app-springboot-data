package com.example.configuration;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhouwei on 2018/9/5
 **/
@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.addresses}")
    private String addresses;

    @Value("${elasticsearch.schema}")
    private String schema;


    @Bean
    public RestHighLevelClient client() {
        HttpHost[] httpHostArr = getHttpHost(addresses, schema);
        RestClientBuilder builder = RestClient.builder(httpHostArr);
        return new RestHighLevelClient(builder);
    }

    private HttpHost[] getHttpHost(String addresses, String schema){
        String[] addressArr = addresses.split(",");
        HttpHost[] httpHostArr = new HttpHost[addressArr.length];
        for(int i=0;i<addressArr.length;i++) {
            String[] nodeAddress = addressArr[i].split(":");
            String ip = nodeAddress[0];
            String port =nodeAddress[1];
            HttpHost httpHost = new HttpHost(ip, Integer.parseInt(port), schema);
            httpHostArr[i]=httpHost;
        }
        return httpHostArr;
    }

}
