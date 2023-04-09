package com.bot.bean;

import lombok.Data;

@Data
public class GetCurrentTrackingOrderListBean {

    private int pageNum;

    private int pageSize;

    private String symbol;

}
