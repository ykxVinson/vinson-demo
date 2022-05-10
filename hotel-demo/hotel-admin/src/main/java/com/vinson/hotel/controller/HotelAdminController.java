package com.vinson.hotel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vinson.hotel.constants.MqConstants;
import com.vinson.hotel.utils.ListResult;
import com.vinson.hotel.utils.ResultDTO;
import com.vinson.hotel.entity.Hotel;
import com.vinson.hotel.service.interfaces.IHotelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@CrossOrigin
@Api(tags = "Hotel")
@RequestMapping("hotel")
public class HotelAdminController {

    @Autowired
    private IHotelService hotelService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @ApiOperation(value = "Get hotel by Id.")
    @GetMapping("{id}")
    public ResultDTO<Hotel> getById(@PathVariable Long id) {
      return ResultDTO.SUCCESS(hotelService.getById(id));
    }

    @ApiOperation(value = "get hotel list")
    @GetMapping("list")
    public ResultDTO<ListResult<Hotel>> getHotels(
            @RequestParam(value = "page", defaultValue = "1") Long page,
            @RequestParam(value = "size", defaultValue = "1") Long size) {
        // 如果要成功分页，需要定义MyBatisPlusConfig 文件才能起作用
        Page<Hotel> result = hotelService.page(new Page<>(page, size));
        return ResultDTO.SUCCESS(new ListResult<>(page, size, result.getTotal(), result.getRecords()));
    }

    @ApiOperation(value = "update hotel by id.")
    @PutMapping
    public ResultDTO updateById(@RequestBody Hotel hotel){
        hotelService.updateById(hotel);
        rabbitTemplate.convertAndSend(MqConstants.HOTEL_EXCHANGE, MqConstants.HOTEL_INSERT_KEY, hotel.getId());
        return ResultDTO.SUCCESS();
    }

    @ApiOperation(value = "add hotel.")
    @PostMapping
    public ResultDTO addHotel(@RequestBody Hotel hotel){
        hotelService.save(hotel);
        rabbitTemplate.convertAndSend(MqConstants.HOTEL_EXCHANGE,MqConstants.HOTEL_INSERT_KEY, hotel.getId());
        return ResultDTO.SUCCESS();
    }

    @ApiOperation(value = "delete hotel by id.")
    @DeleteMapping("{id}")
    public ResultDTO deleteById(@PathVariable("id") Long id){
        hotelService.removeById(id);
        rabbitTemplate.convertAndSend(MqConstants.HOTEL_EXCHANGE, MqConstants.HOTEL_DELETE_KEY, id);
        return ResultDTO.SUCCESS();
    }
}
