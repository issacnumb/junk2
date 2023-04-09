package com.bot.bean;

import lombok.Data;

@Data
public class SetLevelBean {

    private String symbol;

    //杠杆倍数，可填写1-100之间的整数
    private Integer leverage;

    //持仓方向（1-多仓，2-空仓）
    private Integer side;

    //持仓方向 （1-多仓，2-空仓） 全仓时此字段可以不传值  对我来说为空
    private Integer holdSide;

}
