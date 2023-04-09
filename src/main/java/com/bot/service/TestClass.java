package com.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestClass {

    @Autowired
    public MessageHandler messageHandler;

    public void testFunctionAfterSystemInit() {
        log.info("testFunctionAfterSystemInit--------start");

        while (true) {
            String msg = messageHandler.takeMessage();
            if (msg != null) {
                log.info("getMessage : {}", msg);
            }
        }
    }

}
