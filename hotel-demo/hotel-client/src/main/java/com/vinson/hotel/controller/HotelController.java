package com.vinson.hotel.controller;

import com.vinson.hotel.domain.Hotel;
import com.vinson.hotel.domain.PageResult;
import com.vinson.hotel.domain.RequestParams;
import com.vinson.hotel.service.interfaces.IHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("hotel")
public class HotelController {

    @Autowired
    private IHotelService iHotelService;

    @GetMapping("list")
    public List<Hotel> getAll(){
        return iHotelService.list();
    }

    @PostMapping("list")
    public PageResult search(@RequestBody RequestParams params){
        return iHotelService.search(params);
    }

    @PostMapping("filters")
    public Map<String, List<String>> getFilters(@RequestBody RequestParams params){
        return iHotelService.filters(params);
    }

    @GetMapping("suggestion")
    public List<Map<String,String>> getSuggestions(@RequestParam("key") String prefix){
        return iHotelService.getSuggestions(prefix);
    }
}
