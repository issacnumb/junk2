package com.bot.bean;

import lombok.Data;

import java.util.List;

@Data
public class CloseOrderBean {

    private String symbol;

    private List<String> trackingNos;

}
