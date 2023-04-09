package com.bot.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlaceOrderBean implements PlaceOrderInterface {

    private String symbol;
    private String client_oid;
    private String size;
    //1多2空
    private String type;
    //0:普通，1：只做maker;2:全部成交或立即取消;3:立即成交并取消剩余
    private String order_type;
    //0:限价还是1:市价
    private String match_price;
    //-----------------------非必填------------------------
    private String price;
    private BigDecimal presetTakeProfitPrice;
    private BigDecimal presetStopLossPrice;

}
