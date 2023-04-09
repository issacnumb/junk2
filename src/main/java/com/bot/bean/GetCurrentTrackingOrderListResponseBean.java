package com.bot.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GetCurrentTrackingOrderListResponseBean {

    private boolean success;

    private String code;

    private String message;

    private String msgParams;

    private boolean systemError;

    private OutData data;


    @Data
    public class OutData {

        private int total;

        private List<InnerData> data;


    }

    @Data
    public class InnerData {

        private String productCode;

        private String orderNo;

        private String openOrderNo;

        private BigDecimal averageOpenPrice;

        private String holdSide;

    }

}
