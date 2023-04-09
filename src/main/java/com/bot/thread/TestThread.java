package com.bot.thread;

import com.bot.constant.ParamNote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;


@Slf4j
@Component
public class TestThread {

    @Autowired
    public ParamNote paramNote;

    @Async
    public CompletableFuture<String> work() {

        return CompletableFuture.supplyAsync(() -> {

            log.info("------------Thread start---------------");

            long s = paramNote.getCommonSleepTime();
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 60 * 1000) {
                try {
                    Thread.sleep(1000);
                    System.out.println(s);
                    System.out.println("Thread running");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            log.info("------------Thread end---------------");

            return "Task Result";
        });


    }
}
