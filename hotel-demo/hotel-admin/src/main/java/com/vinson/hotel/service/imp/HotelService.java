package com.vinson.hotel.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vinson.hotel.entity.Hotel;
import com.vinson.hotel.mapper.IHotelMapper;
import com.vinson.hotel.service.interfaces.IHotelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HotelService extends ServiceImpl<IHotelMapper, Hotel> implements IHotelService {

    @Autowired
    IHotelMapper hotelMapper;
}
