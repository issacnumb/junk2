package com.bot.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BianLastPriceResponsetBean {

    private String symbol;

    private BigDecimal price;

}
