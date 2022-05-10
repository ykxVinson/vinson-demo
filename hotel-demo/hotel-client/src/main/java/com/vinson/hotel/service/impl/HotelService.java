package com.vinson.hotel.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vinson.hotel.domain.Hotel;
import com.vinson.hotel.domain.HotelDoc;
import com.vinson.hotel.domain.PageResult;
import com.vinson.hotel.domain.RequestParams;
import com.vinson.hotel.mapper.HotelMapper;
import com.vinson.hotel.service.es.HotelHelper;
import com.vinson.hotel.service.interfaces.IHotelService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private HotelHelper hotelHelper;

    @Override
    public PageResult search(RequestParams params) {
        try {
            log.info("search -> params: "+params);
            //1. 准备Request
            SearchRequest request = new SearchRequest("hotel");
            //2. 准备DSL
            //2.1 查询条件
            // 构建BooleanQuery
            buildHotelQuery(params, request);

            //2.2 分页
            int page = params.getPage();
            int size = params.getSize();
            request.source().from((page - 1) * size).size(size);

            //3 排序
            String location = params.getLocation();
            if(location!=null && !location.equals("")){
                request.source().sort(SortBuilders
                        .geoDistanceSort("location",new GeoPoint(location))
                        .order(SortOrder.ASC)
                        .unit(DistanceUnit.KILOMETERS));
            }

            //3. 发送请求，得到响应
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            //4. 解析响应
            return handleResponse(response);
        }catch (IOException e){
            throw new RuntimeException();
        }
    }

    @Override
    public Map<String, List<String>> filters(RequestParams params) {
        try {
            //1. 准备Request
            SearchRequest request = new SearchRequest("hotel");
            //2. 准备DSL
            //2.1 先进行条件查询
            buildHotelQuery(params, request);
            //2.2再进行聚合
            request.source().size(0);
            hotelHelper.buildAggregationDSL(request);
            //3. 发送请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            //4. 解析结果
            Map<String,List<String>> results = hotelHelper.parseAggregationResponse(response);
            return results;
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String,String>> getSuggestions(String prefix) {
        try {
            List<Map<String,String>> list = new ArrayList<>();
            SearchRequest request = new SearchRequest("hotel");
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(prefix)
                            .skipDuplicates(true)
                            .size(10)
            ));
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Suggest suggest = response.getSuggest();
            CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
            List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
            for (CompletionSuggestion.Entry.Option option : options){
                Map<String, String> value = new HashMap<>();
                String text = option.getText().toString();
                value.put("value",text);
                value.put("label",text);
                list.add(value);
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            //1. 准备Request
            DeleteRequest request = new DeleteRequest("hotel", id.toString());
            //2. 准备发送请求
            client.delete(request, RequestOptions.DEFAULT);
        }catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void insertById(Long id) {
        try {
            //0. 先通过id查酒店数据
            Hotel hotel = getById(id);
            HotelDoc hotelDoc = new HotelDoc(hotel);
            //1. 准备Request
            IndexRequest request = new IndexRequest("hotel").id(hotel.getId().toString());
            //2. 准备DSL
            request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);
            //3. 发送请求
            client.index(request, RequestOptions.DEFAULT);
        }catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    private void buildHotelQuery(RequestParams params, SearchRequest request){
        // 1. 原始查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //关键字搜索
        String key = params.getKey();
        if (key == null || "".equals(key)){
            boolQuery.must(QueryBuilders.matchAllQuery());
        }else{
            boolQuery.must(QueryBuilders.matchQuery("all", key));
        }
        // 条件过滤-- 城市
        if(params.getCity() !=null && !params.getCity().equals("")){
            boolQuery.filter(QueryBuilders.termQuery("city", params.getCity()));
        }
        // 条件过滤 -- 品牌
        if(params.getBrand() !=null && !params.getBrand().equals("")){
            boolQuery.filter(QueryBuilders.termQuery("brand", params.getBrand()));
        }
        // 条件过滤 -- 星级
        if(params.getStarName() !=null && !params.getStarName().equals("")){
            boolQuery.filter(QueryBuilders.termQuery("starName", params.getStarName()));
        }
        // 条件过滤 -- 价格
        if(params.getMinPrice() !=null && params.getMaxPrice() !=null){
            boolQuery.filter(QueryBuilders
                    .rangeQuery("price")
                    .gte(params.getMinPrice())
                    .lte(params.getMaxPrice()));
        }

        //2. 算分查询
        FunctionScoreQueryBuilder functionScoreQuery =
                QueryBuilders.functionScoreQuery(
                        boolQuery,
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                        //过滤条件
                                        QueryBuilders.termQuery("isAD", true),
                                        //算分函数 *10
                                         ScoreFunctionBuilders.weightFactorFunction(100)
                                )
                        });

        request.source().query(functionScoreQuery);
    }

    private PageResult handleResponse(SearchResponse response){
        SearchHits searchHits = response.getHits();
        //4.1 获取总条数
        long total = searchHits.getTotalHits().value;
        //4.2 文档数组
        SearchHit[] hits = searchHits.getHits();
        //4.3 遍历数组
        List<HotelDoc> hotels = new ArrayList<>();
        for (SearchHit hit : hits){
            String json = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            hotelDoc.setDistance(0.00);
            //获取排序值
            Object[] sortValues = hit.getSortValues();
            if(sortValues.length>0){
                Object sortValue = sortValues[0];
                hotelDoc.setDistance(sortValue);
            }
            hotels.add(hotelDoc);
        }
        //4.4 封装返回值
        return new PageResult(total,hotels);
    }
}
