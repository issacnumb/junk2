package com.bot.constant;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Data
public class ParamNote {

    @Value("${apiKey}")
    private String apiKey;
    @Value("${apiSecret}")
    private String apiSecret;
    @Value("${apiPassphrase}")
    private String apiPassphrase;
    @Value("${commonSleepTime}")
    private long commonSleepTime;
    @Value("${baseUrl}")
    private String baseUrl;
    @Value("${placeOrderUrl}")
    private String placeOrderUrl;
    @Value("${closeOrderUrl}")
    private String closeOrderUrl;
    @Value("${setLevelUrl}")
    private String setLevelUrl;
    @Value("${getCurrentTrackingOrderUrl}")
    private String getCurrentTrackingOrderUrl;


    @Value("${ethEnable}")
    private boolean ethEnable;
    @Value("${btcEnable}")
    private boolean btcEnable;

    @Value("${ethOrderLongEnable}")
    private boolean ethOrderLongEnable;
    @Value("${ethOrderShortEnable}")
    private boolean ethOrderShortEnable;

    @Value("${btcOrderLongEnable}")
    private boolean btcOrderLongEnable;
    @Value("${btcOrderShortEnable}")
    private boolean btcOrderShortEnable;

    @Value("${ethOrderAmount}")
    private int ethOrderAmount;
    @Value("${ethTP}")
    private BigDecimal ethTP;
    @Value("${ethSL}")
    private BigDecimal ethSL;

    @Value("${btcOrderAmount}")
    private int btcOrderAmount;
    @Value("${btcTP}")
    private BigDecimal btcTP;
    @Value("${btcSL}")
    private BigDecimal btcSL;

    @Value("${debugLevel}")
    private Boolean debugLevel;



}
