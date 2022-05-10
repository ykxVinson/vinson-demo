package com.vinson.hotel.utils;

import lombok.Data;

import java.util.List;

@Data
public class ListResult<T> {
    private Long page;
    private Long size;
    private Long total;
    private List<T> data;

    public ListResult(Long page,Long size,Long total, List<T> d){
        this.page = page;
        this.size = size;
        this.total = total;
        this.data = d;
    }
}
