package com.bot.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CloseOrderResponseBean {

    private boolean success;

    private String code;

    private String message;

    private String msgParams;

    private boolean systemError;

    private List<InnerData> data;

    @Data
    public class InnerData {
        private String trackingNo;
        private boolean result;
    }

}
