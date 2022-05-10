package com.vinson.hotel.service.es;

import com.vinson.hotel.constants.HotelConstants;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HotelHelper {

    /**
     * 组装聚合条件的DSL语句
     * @param request
     */
    public void buildAggregationDSL(SearchRequest request){
        Iterator<String> iterator = HotelConstants.filterNameList.keySet().iterator();
        while (iterator.hasNext()) {
            String filter = iterator.next();
            request.source().aggregation(AggregationBuilders
                    .terms(String.join("", filter, "Agg"))
                    .field(filter)
                    .size(20));
        }
    }

    /**
     * 解析聚合条件返回的结果
     * @param response
     * @return
     */
    public Map<String, List<String>> parseAggregationResponse(SearchResponse response){
        Map<String, List<String>> list = new HashMap<>();
        Iterator<String> iterator = HotelConstants.filterNameList.keySet().iterator();
        while (iterator.hasNext()) {
            String filter = iterator.next();
            Aggregations aggregations = response.getAggregations();
            Terms terms = aggregations.get(String.join("", filter, "Agg"));
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            List<String> bucketList = new ArrayList<>();
            for (Terms.Bucket bucket : buckets) {
                String key = bucket.getKeyAsString();
                bucketList.add(key);
            }
            list.put(HotelConstants.filterNameList.get(filter),bucketList);
        }
     return list;
    }
}
