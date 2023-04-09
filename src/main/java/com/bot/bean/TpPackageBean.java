package com.bot.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

@Data
public class TpPackageBean {

    //------------------------------------开仓返回的数据---------------------------------
    private String productCode;
    private String client_oid;//客户端标识 本地订单号
    private String openOrderId;//开仓返回的,开仓订单号
    private Integer side;//多空方向 1多 2空
    private Boolean isPositionControl;//是否为 档位控制订单  true是  false否

    //--------------------------------查询当前带单后返回的数据-----------------------------
    private String orderNo;//查询带单中对应的orderId
    private BigDecimal openPrice;//订单开仓价格

    //---------------------------------持仓扫描后填补的数据-------------------------------
    private Integer tpSlIndex;//该订单当前的档位

    //---------------------------------每次更新标识--------------------------------------
    private String updateSign;

    //---------------------------------订单淘汰积分--------------------------------------
    private Integer disusePoint = 0;

//    public static void main(String[] args) {
//        ArrayList<Person> s = new ArrayList();
//        s.add(new Person("a"));
//        s.add(new Person("b"));
//        s.add(new Person("c"));
//        s.add(new Person("d"));
//
//        Iterator<Person> iterator = s.iterator();
//
//        while (iterator.hasNext()) {
//            Person temp = iterator.next();
//            if (temp.getName().equals("b")) {
//                temp.setName("modified");
//            }
//        }
//
//        System.out.println(s);
//
//    }
//
//    @Data
//    public static class Person {
//        private String name;
//
//        Person(String a) {
//            this.name = a;
//        }
//    }

}
