package com.vinson.hotel.mq;

import com.vinson.hotel.constants.MqConstants;
import com.vinson.hotel.service.interfaces.IHotelService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HotelListener {

    @Autowired
    private IHotelService hotelService;

    /**
     *  监听酒店新增或修改的业务
     * @param id 酒店的id
     */
    @RabbitListener(queues = MqConstants.HOTEL_INSERT_QUEUE)
    public void listenerHotelInsertOrUpdate(Long id){
        hotelService.insertById(id);
    }

    /**
     *  监听酒店删除的业务
     * @param id 酒店的id
     */
    @RabbitListener(queues = MqConstants.HOTEL_DELETE_QUEUE)
    public void listenerHotelDelete(Long id){
        hotelService.deleteById(id);
    }

}
