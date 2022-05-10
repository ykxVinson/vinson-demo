package com.vinson.hotel;


import com.alibaba.fastjson.JSON;
import com.vinson.hotel.domain.Hotel;
import com.vinson.hotel.domain.HotelDoc;
import com.vinson.hotel.service.interfaces.IHotelService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class HotelDocumentTest {

    @Autowired
    private IHotelService hotelService;

    private RestHighLevelClient client;

    @BeforeEach
    void setUp() {
        this.client = new RestHighLevelClient(RestClient.builder(
           HttpHost.create("http://192.168.40.129:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }

    //新增文档
    @Test
    void tesetAddDocument() throws IOException {
        // 根据id从数据库中查找该对象
       Hotel hotel =  hotelService.getById(61083);
       // 转换为文档对象
        HotelDoc hotelDoc = new HotelDoc(hotel);
        //1. 准备Request对象
        IndexRequest request = new IndexRequest("hotel").id(hotelDoc.getId().toString());
        //2. 准备Json文档
        request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);
        //3. 发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    //查询文档
    @Test
    void testGetDocument() throws IOException {
        //1. 准备Request
        GetRequest request = new GetRequest("hotel","61083");
        //2. 发送请求，得到响应
        GetResponse response = client.get(request,RequestOptions.DEFAULT);
        //3. 解析响应结果
        String json = response.getSourceAsString();

        HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
        System.out.println(hotelDoc);
    }

    // 更新文档
    @Test
    void testUpdateDocument() throws IOException {

        UpdateRequest request = new UpdateRequest("hotel", "61083");

        request.doc(
                "price","952",
                "starName","四钻"
        );

        client.update(request, RequestOptions.DEFAULT);
    }

    //删除文档
    @Test
    void testDeleteDocument() throws IOException {

        DeleteRequest request = new DeleteRequest("hotel", "61083");
        client.delete(request,RequestOptions.DEFAULT);
    }

    //批量插入hotel数据
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest request = new BulkRequest();

        //批量查询数据
        List<Hotel> hotels = hotelService.list();
        for(Hotel hotel : hotels){
            HotelDoc hotelDoc = new HotelDoc(hotel);
            request.add(new IndexRequest("hotel")
                    .id(hotelDoc.getId().toString())
                    .source(JSON.toJSONString(hotelDoc),XContentType.JSON));
        }

        client.bulk(request, RequestOptions.DEFAULT);
    }
}
