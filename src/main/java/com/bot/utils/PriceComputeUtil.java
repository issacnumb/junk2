package com.bot.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceComputeUtil {

    //输入 开仓价格 多空 盈利点 -- 返回 该盈损点对应的价格
    // side 1多   2空
    public static BigDecimal computeWinPrice(BigDecimal oriPrice, BigDecimal point, int side) {
        if (side == 1) {
            return oriPrice.multiply(BigDecimal.ONE.add(point));
        } else {
            return oriPrice.multiply(BigDecimal.ONE.subtract(point));
        }
    }


}
