package com.vinson.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vinson.hotel.entity.Hotel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IHotelMapper extends BaseMapper<Hotel> {
}
