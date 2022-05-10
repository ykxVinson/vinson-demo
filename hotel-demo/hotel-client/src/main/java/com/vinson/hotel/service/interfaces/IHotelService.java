package com.vinson.hotel.service.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vinson.hotel.domain.Hotel;
import com.vinson.hotel.domain.PageResult;
import com.vinson.hotel.domain.RequestParams;

import java.util.List;
import java.util.Map;

public interface IHotelService extends IService<Hotel> {
    PageResult search(RequestParams params);

    Map<String, List<String>> filters(RequestParams params);

    List<Map<String,String>> getSuggestions(String prefix);

    void deleteById(Long id);

    void insertById(Long id);
}
