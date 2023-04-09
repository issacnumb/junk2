package com.bot.controller;

import com.bot.storage.CommonInfoStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/test")
public class TestController {
    @Autowired
    private CommonInfoStorage storage;

    @RequestMapping("/getCurrentOrderList")
    public Object getCurrentOrderList() {

        return storage.getCurrentPosition();

    }

}
