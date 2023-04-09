package com.bot.service;

import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;
import com.bot.constant.ParamNote;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.bot.storage.CommonInfoStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BianService implements BiAnServiceConst {

    @Autowired
    private ParamNote paramNote;

    @Autowired
    private CommonInfoStorage storage;

    private Gson gson = new Gson();
    private UMWebsocketClientImpl client = new UMWebsocketClientImpl();

    private Integer ehtSockId = null;
    private Integer btcSockId = null;

    public void getLastPriceFromBiAn() {
        //----------------------------------------------------ETH-------------------------------------------------------
        if (paramNote.isEthEnable() && ehtSockId == null) {
            ehtSockId = client.symbolTicker("ethusdt", ((event) -> {
                JsonObject asJsonObject = JsonParser.parseString(event).getAsJsonObject();
                storage.setEthPriceFromBiAn(new BigDecimal(asJsonObject.get("c").toString().replace("\"", "")));
            }));
        }

        if (!paramNote.isEthEnable() && ehtSockId != null) {
            client.closeConnection(ehtSockId);
            storage.setEthPriceFromBiAn(null);
        }

        //----------------------------------------------------BTC-------------------------------------------------------
        // TODO 暂时关闭BTC价格服务
//        if (paramNote.isBtcEnable() && btcSockId == null) {
//            btcSockId = client.symbolTicker("btcusdt", ((event) -> {
//                JsonObject asJsonObject = JsonParser.parseString(event).getAsJsonObject();
//                storage.setBtcPriceFromBiAn(new BigDecimal(asJsonObject.get("c").toString().replace("\"", "")));
//            }));
//        }
//
//        if (!paramNote.isBtcEnable() && btcSockId != null) {
//            client.closeConnection(btcSockId);
//            storage.setBtcPriceFromBiAn(null);
//        }
    }

    public static void main(String[] args) {
        UMWebsocketClientImpl client = new UMWebsocketClientImpl();
        int streamId2 = client.symbolTicker("ethusdt", ((event) -> {
            long start = System.currentTimeMillis();
            JsonObject asJsonObject = JsonParser.parseString(event).getAsJsonObject();
            System.out.println(asJsonObject.get("c"));
            System.out.println(new BigDecimal(asJsonObject.get("c").toString().replace("\"", "")));
            System.out.println(System.currentTimeMillis() - start);
//            client.closeAllConnections();
        }));

//        Gson gson = new Gson();
//        long start = System.currentTimeMillis();
//        String w = "{\"e\":\"aggTrade\",\"E\":1677648010183,\"a\":1228630123,\"s\":\"ETHUSDT\",\"p\":\"1651.53\",\"q\":\"2.423\",\"f\":2742508346,\"l\":2742508347,\"T\":1677648010028,\"m\":false}";
//
//
//        JsonObject asJsonObject = JsonParser.parseString(w).getAsJsonObject();
//        System.out.println(System.currentTimeMillis() - start);
//        System.out.println(asJsonObject.get("p"));


    }
}
