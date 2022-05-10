package com.vinson.user.controller;

import com.vinson.user.config.PatternProperties;
import com.vinson.user.domain.User;
import com.vinson.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private PatternProperties patternProperties;

    @GetMapping("{id}")
    public User getUserById(@PathVariable Integer id, @RequestHeader(value = "Truth", required = false) String truth){
        System.out.println("truth: " + truth);
        return iUserService.getById(id);
    }

    @GetMapping("now")
    public String now(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(patternProperties.getDateformat()));
    }

    @GetMapping("prop")
    public PatternProperties properties(){
        return patternProperties;
    }
}
