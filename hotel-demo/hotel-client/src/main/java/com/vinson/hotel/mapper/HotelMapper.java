package com.vinson.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vinson.hotel.domain.Hotel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HotelMapper extends BaseMapper<Hotel> {
}
