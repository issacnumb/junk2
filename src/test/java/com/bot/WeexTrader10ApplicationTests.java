package com.bot;

import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;
import com.bot.service.WeexService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class WeexTrader10ApplicationTests {

    @Autowired
    WeexService weexService;

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) {
        /*
        {
          "e": "aggTrade",  // 事件类型
          "E": 123456789,   // 事件时间
          "s": "BNBUSDT",    // 交易对
          "a": 5933014,     // 归集成交 ID
          "p": "0.001",     // 成交价格
          "q": "100",       // 成交量   P   就可以当做最新价格
          "f": 100,         // 被归集的首个交易ID
          "l": 105,         // 被归集的末次交易ID
          "T": 123456785,   // 成交时间
          "m": true         // 买方是否是做市方。如true，则此次成交是一个主动卖出单，否则是一个主动买入单。
        }
         */
        UMWebsocketClientImpl client = new UMWebsocketClientImpl();
//        int streamId1 = client.aggTradeStream("btcusdt", ((event) -> {
//            System.out.println(event);
//        }));
        int streamId2 = client.aggTradeStream("ethusdt", ((event) -> {
            System.out.println(event);//其中P就可以当做最新价格
        }));
//        client.closeConnection(streamId1);
//        client.closeConnection(streamId2);

        int i = 0;
        while (true) {
            if (i == 5000) {
                System.out.println("GOT YOU");
            }
            i++;
        }
    }

//    @Test
//    void placeOrder() {
//
//        PlaceOrderBean placeOrderBean = new PlaceOrderBean();
//        placeOrderBean.setSymbol("cmt_ethusdt");
//        placeOrderBean.setClient_oid("cmt_ethusdt" + System.currentTimeMillis());
//        placeOrderBean.setSize(String.valueOf(1));
//        placeOrderBean.setType(String.valueOf(2));
//        placeOrderBean.setOrder_type(String.valueOf(2));
//        placeOrderBean.setMatch_price(String.valueOf(1));
//
//        String s = weexService.placeOrder(placeOrderBean);
//        log.info(s);
//    }

}
