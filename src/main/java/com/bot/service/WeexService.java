package com.bot.service;

import com.bot.bean.*;
import com.bot.constant.ParamNote;
import com.bot.utils.Coder;
import com.bot.utils.HttpClientUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class WeexService {

    @Autowired
    private ParamNote paramNote;
    @Autowired
    private Coder coder;

    Gson gson = new Gson();


    //下单
    public PlaceOrderResponseBean placeOrder(PlaceOrderBean placeOrderBean) {
        String bodyJson = gson.toJson(placeOrderBean);

        Map<String, String> headerMap = generateHeaderMap(paramNote.getPlaceOrderUrl(), "POST", bodyJson);

        String result = null;
        try {
            result = HttpClientUtils.sendPostJson(paramNote.getBaseUrl() + paramNote.getPlaceOrderUrl(), bodyJson, headerMap);
        } catch (Exception e) {
            log.error("placeOrder error : {}", e.getMessage());
            return null;
        }
        return gson.fromJson(result, PlaceOrderResponseBean.class);
    }

    //设置杠杆
    public SetLevelResponseBean setLevel(SetLevelBean setLevelBean) {
        String bodyJson = gson.toJson(setLevelBean);

        Map<String, String> headerMap = generateHeaderMap(paramNote.getSetLevelUrl(), "POST", bodyJson);

        String result = null;

        try {
            result = HttpClientUtils.sendPostJson(paramNote.getBaseUrl() + paramNote.getSetLevelUrl(), bodyJson, headerMap);
        } catch (Exception e) {
            log.error("setLevel error : {}", e.getMessage());
            log.error("setLevel error stack trace: {}", e.getStackTrace());
            return null;
        }
        return gson.fromJson(result, SetLevelResponseBean.class);
    }

    //平仓
    public CloseOrderResponseBean closeOrder(CloseOrderBean closeOrderBean) {
        String bodyJson = gson.toJson(closeOrderBean);

        Map<String, String> headerMap = generateHeaderMap(paramNote.getCloseOrderUrl(), "POST", bodyJson);

        String result = null;

        try {
            result = HttpClientUtils.sendPostJson(paramNote.getBaseUrl() + paramNote.getCloseOrderUrl(), bodyJson, headerMap);
        } catch (Exception e) {
            log.error("closeOrder error : {}", e.getMessage());
            return null;
        }
        return gson.fromJson(result, CloseOrderResponseBean.class);
    }

    //获取当前带单仓位及均价
    public GetCurrentTrackingOrderListResponseBean getCurrentTrackingOrderList(GetCurrentTrackingOrderListBean getCurrentTrackingOrderListBean) {
        String bodyJson = gson.toJson(getCurrentTrackingOrderListBean);

        Map<String, String> headerMap = generateHeaderMap(paramNote.getGetCurrentTrackingOrderUrl(), "POST", bodyJson);

        String result = null;

        try {
            result = HttpClientUtils.sendPostJson(paramNote.getBaseUrl() + paramNote.getGetCurrentTrackingOrderUrl(), bodyJson, headerMap);
        } catch (Exception e) {
            log.error("getCurrentTrackingOrderList error : {}", e.getMessage());
            return null;
        }
        return gson.fromJson(result, GetCurrentTrackingOrderListResponseBean.class);
    }

    private Map<String, String> generateHeaderMap(String pathWithGetParam, String methodName, String bodyJson) {
        long time_stamp = new Date().getTime();
        String ori_sign = time_stamp + methodName + pathWithGetParam + (bodyJson == null ? "" : bodyJson);
        String sign = null;
        try {
            sign = coder.HMAC_SHA256_BASE64(ori_sign, paramNote.getApiSecret());
        } catch (NoSuchAlgorithmException e) {
            log.error("encode fail  [1] info  : {}", e.getMessage());
            return null;
        } catch (InvalidKeyException e) {
            log.error("encode fail  [2] info  : {}", e.getMessage());
            return null;
        }
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("ACCESS-KEY", paramNote.getApiKey());
        headerMap.put("ACCESS-TIMESTAMP", String.valueOf(time_stamp));
        headerMap.put("ACCESS-PASSPHRASE", paramNote.getApiPassphrase());
        headerMap.put("Content-Type", "application/json");
        headerMap.put("locale", "zh-CN");
        headerMap.put("ACCESS-SIGN", sign);
        return headerMap;
    }

}
