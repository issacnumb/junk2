package com.bot.controller;

import com.bot.constant.ParamNote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/btcParamSet")
public class BtcParamSetController {

    @Autowired
    private ParamNote paramNote;

    /**
     * 修改 BTC 开仓量
     */
    @RequestMapping("/amount")
    public String setBtcAmount(Integer amount) {
        if (amount == null) {
            return "参数为空,修改失败";
        }
        paramNote.setBtcOrderAmount(amount);
        return "修改成功, BTC 下单量已经修改为 : " + amount;
    }

    /**
     * 允许 BTC 策略做多
     */
    @RequestMapping("/btcEnableLong")
    public String ethEnableLong() {
        paramNote.setBtcOrderLongEnable(true);
        return "修改成功,BTC [允许] 做多";
    }

    /**
     * 禁止 BTC 策略做多
     */
    @RequestMapping("/btcDisableLong")
    public String btcDisableLong() {
        paramNote.setBtcOrderLongEnable(false);
        return "修改成功,BTC [禁止] 做多";
    }

    /**
     * 允许 BTC 策略做空
     */
    @RequestMapping("/btcEnableShort")
    public String btcEnableShort() {
        paramNote.setBtcOrderShortEnable(true);
        return "修改成功,BTC [允许] 做空";
    }

    /**
     * 禁止 BTC 策略做空
     */
    @RequestMapping("/btcDisableShort")
    public String btcDisableShort() {
        paramNote.setBtcOrderShortEnable(false);
        return "修改成功,BTC [禁止] 做空";
    }

}
