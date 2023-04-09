package com.bot.service;

import com.bot.thread.TestThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@Slf4j
public class MessageHandler {

    //    private ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
    private List<String> queue = new ArrayList<>();

    public boolean addMessage(String message) {
        try {
            queue.add(message);
            return true;
        } catch (Exception e) {
            log.error("消息入队出错");
            log.error(e.getMessage());
            return false;
        }
    }

    public String takeMessage() {
        try {
            if (queue.size() == 0) {
                return null;
            }
            Iterator<String> iterator = queue.iterator();
            if (iterator.hasNext()) {
                String result = iterator.next();
                iterator.remove();
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("获取消息 并 删除消息 出错");
            log.error(e.getMessage());
            return null;
        }
    }

//    public static void main(String[] args) throws InterruptedException {
//
//        while (true) {
//            TestThread thread = new TestThread();
//            thread.start();
//            thread.join();
//        }
//    }

}
