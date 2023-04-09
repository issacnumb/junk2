package com.bot.bean;

import lombok.Data;

@Data
public class SetLevelResponseBean {

    private String symbol;

    //多仓杠杆
    private String long_leverage;

    //持仓模式 fixed逐仓模式 crossed全仓模式
    private String margin_mode;

    //空仓杠杆
    private String short_leverage;

    //是否是正向合约
    private boolean forwardContractFlag;

}
