package com.bot.controller;

import com.bot.bean.*;
import com.bot.service.MessageHandler;
import com.bot.service.WeexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/")
public class MessageController implements MessageControllerConst {

    @Autowired
    private MessageHandler messageHandler;
    @Autowired
    private WeexService weexService;

    @RequestMapping("/webhook")
    public void receiveMessage(@RequestBody String message) {
        log.info("receiveMessage ： {}", message);
        messageHandler.addMessage(message);
    }

    @RequestMapping("/wisdom_price")
    public void receiveWisdom_price(String message) {
        log.info("receiveMessage ： {}", message);
        messageHandler.addMessage(message);
    }

    @RequestMapping("/testLong")
    public void testLong() {
        PlaceOrderBean placeOrderBean = new PlaceOrderBean();
        placeOrderBean.setSymbol("cmt_ethusdt");
        placeOrderBean.setClient_oid("cmt_ethusdt" + System.currentTimeMillis());
        placeOrderBean.setSize(String.valueOf(1));
        placeOrderBean.setType(String.valueOf(2));
        placeOrderBean.setOrder_type(String.valueOf(2));
        placeOrderBean.setMatch_price(String.valueOf(1));

        PlaceOrderResponseBean placeOrderResponse = weexService.placeOrder(placeOrderBean);
        log.info("placeOrder return : ", placeOrderResponse);
    }

    @RequestMapping("/testCloseAll")
    public void testCloseLong() {

        GetCurrentTrackingOrderListBean currentTrackingOrderListBean = new GetCurrentTrackingOrderListBean();
        currentTrackingOrderListBean.setPageSize(50);
        currentTrackingOrderListBean.setSymbol("cmt_ethusdt");
        currentTrackingOrderListBean.setPageSize(1);

        GetCurrentTrackingOrderListResponseBean currentTrackingOrderList = weexService.getCurrentTrackingOrderList(currentTrackingOrderListBean);

        GetCurrentTrackingOrderListResponseBean.OutData data = currentTrackingOrderList.getData();
        List<GetCurrentTrackingOrderListResponseBean.InnerData> data1 = data.getData();

        List<String> paramList = new ArrayList<>();

        for (int index = 0 ; index < data1.size(); index++) {
            paramList.add(data1.get(index).getOrderNo());
        }

        CloseOrderBean closeOrderBean = new CloseOrderBean();
        closeOrderBean.setSymbol("cmt_ethusdt");
        closeOrderBean.setTrackingNos(paramList);

        weexService.closeOrder(closeOrderBean);
    }

}


