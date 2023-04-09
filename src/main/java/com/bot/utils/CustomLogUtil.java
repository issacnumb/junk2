package com.bot.utils;

import com.bot.constant.ParamNote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomLogUtil {

    @Autowired
    private ParamNote paramNote;

    public void recordMoreDetail (String recordContent) {
        if (paramNote.getDebugLevel()) {
            log.warn(recordContent);
        }
    }

}
