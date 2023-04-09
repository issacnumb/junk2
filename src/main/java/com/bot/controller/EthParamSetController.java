package com.bot.controller;

import com.bot.constant.ParamNote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/ethParamSet")
public class EthParamSetController {

    @Autowired
    private ParamNote paramNote;

    /**
     * 修改 ETH 开仓量
     */
    @RequestMapping("/amount")
    public String setEthAmount(Integer amount) {
        if (amount == null) {
            return "参数为空,修改失败";
        }
        paramNote.setEthOrderAmount(amount);
        return "修改成功,ETH下单量已经修改为 : " + amount;
    }

    /**
     * 允许 ETH策略 做多
     */
    @RequestMapping("/ethEnableLong")
    public String ethEnableLong() {
        paramNote.setEthOrderLongEnable(true);
        return "修改成功,ETH [允许] 做多";
    }

    /**
     * 禁止 ETH策略 做多
     */
    @RequestMapping("/ethDisableLong")
    public String ethDisableLong() {
        paramNote.setEthOrderLongEnable(false);
        return "修改成功,ETH [禁止] 做多";
    }

    /**
     * 允许 ETH策略 做空
     */
    @RequestMapping("/ethEnableShort")
    public String ethEnableShort() {
        paramNote.setEthOrderShortEnable(true);
        return "修改成功,ETH [允许] 做空";
    }

    /**
     * 禁止 ETH策略 做空
     */
    @RequestMapping("/ethDisableShort")
    public String ethDisableShort() {
        paramNote.setEthOrderShortEnable(false);
        return "修改成功,ETH [禁止] 做空";
    }

}
