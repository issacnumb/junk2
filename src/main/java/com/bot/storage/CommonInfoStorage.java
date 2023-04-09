package com.bot.storage;

import com.bot.bean.TPSLbean;
import com.bot.bean.TpPackageBean;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Data
public class CommonInfoStorage {

//---------------------------------------------------TP/SL_BEAN---------------------------------------------------------

    private ArrayList<TPSLbean> TpSlBeanList = new TPSLbean().getTPSLList();

//-------------------------------------------------------ETH------------------------------------------------------------

    //存储  来自于 BiAn          的ETH价格
    private BigDecimal ethPriceFromBiAn = null;

    //存储  来自于 wisdom_price  的ETH价格
    private BigDecimal ethPriceFromWisdom = null;

    //存储  来自于 wisdom_price  ETH时间
    private Long ethWisdomUpdateTime = 0L;

//-------------------------------------------------------BTC------------------------------------------------------------

    //存储  来自于 BiAn          的BTC价格
    private BigDecimal btcPriceFromBiAn = null;

    //存储  来自于 wisdom_price  的BTC价格
    private BigDecimal btcPriceFromWisdom = null;

    //存储  来自于 wisdom_price  BTC时间
    private Long btcWisdomUpdateTime = 0L;

//---------------------------------------------------POSITIONS----------------------------------------------------------

    // TODO 这里的做出了修改,   需要检测内存变化
    private CopyOnWriteArrayList<TpPackageBean> currentPosition = new CopyOnWriteArrayList<>();
//    private List<TpPackageBean> currentPosition = new ArrayList<>();

    public BigDecimal getEthLastPrice() {
        if (ethPriceFromWisdom != null && ethWisdomUpdateTime != 0 && (System.currentTimeMillis() - ethWisdomUpdateTime) < 2500) {
            return ethPriceFromWisdom;
        } else {
            if (ethPriceFromBiAn != null) {
                return ethPriceFromBiAn;
            } else {
                return null;
            }
        }
    }

    public BigDecimal getBtcLastPrice() {
        if (btcPriceFromWisdom != null && btcWisdomUpdateTime != 0 && (System.currentTimeMillis() - btcWisdomUpdateTime) < 2500) {
            return btcPriceFromWisdom;
        } else {
            if (btcPriceFromBiAn != null) {
                return btcPriceFromBiAn;
            } else {
                return null;
            }
        }
    }
}
