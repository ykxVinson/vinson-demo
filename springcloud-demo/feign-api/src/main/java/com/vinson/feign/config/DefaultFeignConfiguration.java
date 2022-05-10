package com.vinson.feign.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfiguration {

    @Bean
    public Logger.Level legLevel(){
        return Logger.Level.NONE;
    }
}
