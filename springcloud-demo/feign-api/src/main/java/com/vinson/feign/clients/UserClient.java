package com.vinson.feign.clients;

import com.vinson.feign.config.DefaultFeignConfiguration;
import com.vinson.feign.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "userservice",configuration = DefaultFeignConfiguration.class)
public interface UserClient {

    @GetMapping("/user/{id}")
    User findById(@PathVariable("id") Long id);
}
