package com.vinson.hotel;

import com.alibaba.fastjson.JSON;
import com.vinson.hotel.domain.HotelDoc;
import org.apache.http.HttpHost;
import org.apache.lucene.util.CollectionUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class HotelSerchTest {

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

    @Test
    void testMatchAll() throws IOException {
        //1. 准备request
        SearchRequest request = new SearchRequest("hotel");
        //2. 准备DSL
        request.source().query(QueryBuilders.matchAllQuery());
        //3. 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4. 解析结果
        handleResponse(response);
    }

    @Test
    void testMatch() throws IOException {
        //1. 准备request
        SearchRequest request = new SearchRequest("hotel");
        //2. 准备DSL
        request.source().query(QueryBuilders.matchQuery("all", "如家"));
        //3. 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4. 解析结果
        handleResponse(response);
    }

    @Test
    void testBool() throws IOException {
        //1. 准备request
        SearchRequest request = new SearchRequest("hotel");

        //2. 准备DSL
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("city", "北京"));
        boolQuery.filter(QueryBuilders.rangeQuery("price").lte(250));


        request.source().query(boolQuery);

        //3. 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4. 解析结果
        handleResponse(response);
    }

    @Test
    void testPageAndSort() throws IOException {
        //1. 准备request
        SearchRequest request = new SearchRequest("hotel");

        //2. 准备DSL
        request.source().query(QueryBuilders.matchAllQuery());
        request.source().sort("price", SortOrder.ASC);
        request.source().from(0).size(5);

        //3. 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4. 解析结果
        handleResponse(response);
    }

    @Test
    void testHighlight() throws IOException {
        //1. 准备request
        SearchRequest request = new SearchRequest("hotel");

        //2. 准备DSL
        request.source().query(QueryBuilders.matchQuery("all", "如家"));
        request.source().highlighter(new HighlightBuilder()
                .field("name")
                .requireFieldMatch(false));

        //3. 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4. 解析结果
        handleResponse(response);
    }

    private void handleResponse(SearchResponse response){
        SearchHits searchHits = response.getHits();
        //4.1 获取总条数
        long total = searchHits.getTotalHits().value;
        //4.2 文档数组
        SearchHit[] hits = searchHits.getHits();
        //4.3 遍历数组
        for (SearchHit hit : hits){
            String json = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);

            //获取高亮结果
           Map<String, HighlightField> highlightFieldMap =  hit.getHighlightFields();
           if(!CollectionUtils.isEmpty(highlightFieldMap)){
               //根据字段名获取高亮结果
               HighlightField highlightField = highlightFieldMap.get("name");
               if(!StringUtils.isEmpty(highlightField)) {
                   //获取高亮值
                   String name = highlightField.getFragments()[0].string();
                   hotelDoc.setName(name);
               }
           }

            System.out.println(hotelDoc);
        }
        System.out.println("总条数： "+ total);
    }


    @Test
    void testAggregation() throws IOException {
       SearchRequest request =  new SearchRequest("hotel");

       request.source().size(0);
       request.source().aggregation(AggregationBuilders
               .terms("brandAgg")
               .field("city")
               .size(10));

     SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        Aggregations aggregations =  response.getAggregations();
     Terms terms = aggregations.get("brandAgg");
     List<? extends Terms.Bucket> buckets = terms.getBuckets();
     for(Terms.Bucket bucket : buckets){
         String key = bucket.getKeyAsString();
         System.out.println(key);
     }
    }

    @Test
    void testSuggest() throws IOException {
        //1. 准备Request对象
        SearchRequest request = new SearchRequest("hotel");
        //2. DSL查询
        request.source().suggest(new SuggestBuilder()
                .addSuggestion("suggestions",
                SuggestBuilders.completionSuggestion("suggestion")
                        .prefix("h")
                        .skipDuplicates(true)
                        .size(10)));
        //3. 发送请求
       SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4. 解析结果
        Suggest suggest = response.getSuggest();
        CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
        List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
        for (CompletionSuggestion.Entry.Option option : options){
            String text = option.getText().toString();
            System.out.println(text);
        }
    }
}
