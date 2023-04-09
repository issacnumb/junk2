package com.bot.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;

@Data
public class TPSLbean {

    private BigDecimal TpPoint;
    private BigDecimal SlPoint;

    public TPSLbean() {
    }

    TPSLbean(BigDecimal tp, BigDecimal sl) {
        this.TpPoint = tp;
        this.SlPoint = sl;
    }


    public ArrayList<TPSLbean> getTPSLList() {

        ArrayList<TPSLbean> tpSlList = new ArrayList<>();

//        TPSLbean o001 = new TPSLbean(new BigDecimal(0.003), new BigDecimal(0.002));
        TPSLbean o00 = new TPSLbean(new BigDecimal(0.004), new BigDecimal(0.003));
        TPSLbean o01 = new TPSLbean(new BigDecimal(0.005), new BigDecimal(0.004));
        TPSLbean o02 = new TPSLbean(new BigDecimal(0.007), new BigDecimal(0.005));//0.002
        TPSLbean o03 = new TPSLbean(new BigDecimal(0.008), new BigDecimal(0.006));
        TPSLbean o04 = new TPSLbean(new BigDecimal(0.010), new BigDecimal(0.008));
        TPSLbean o05 = new TPSLbean(new BigDecimal(0.012), new BigDecimal(0.010));
        TPSLbean o06 = new TPSLbean(new BigDecimal(0.014), new BigDecimal(0.012));
        TPSLbean o07 = new TPSLbean(new BigDecimal(0.016), new BigDecimal(0.012));//0.004
        TPSLbean o08 = new TPSLbean(new BigDecimal(0.018), new BigDecimal(0.014));
        TPSLbean o09 = new TPSLbean(new BigDecimal(0.020), new BigDecimal(0.016));
        TPSLbean o10 = new TPSLbean(new BigDecimal(0.022), new BigDecimal(0.018));
        TPSLbean o11 = new TPSLbean(new BigDecimal(0.024), new BigDecimal(0.020));
        TPSLbean o12 = new TPSLbean(new BigDecimal(0.026), new BigDecimal(0.022));//0.004
        TPSLbean o13 = new TPSLbean(new BigDecimal(0.028), new BigDecimal(0.024));
        TPSLbean o14 = new TPSLbean(new BigDecimal(0.030), new BigDecimal(0.026));
        TPSLbean o15 = new TPSLbean(new BigDecimal(0.032), new BigDecimal(0.028));
        TPSLbean o16 = new TPSLbean(new BigDecimal(0.034), new BigDecimal(0.030));
        TPSLbean o17 = new TPSLbean(new BigDecimal(0.036), new BigDecimal(0.031));//0.005
        TPSLbean o18 = new TPSLbean(new BigDecimal(0.038), new BigDecimal(0.033));
        TPSLbean o19 = new TPSLbean(new BigDecimal(0.040), new BigDecimal(0.035));
        TPSLbean o20 = new TPSLbean(new BigDecimal(0.042), new BigDecimal(0.037));
        TPSLbean o21 = new TPSLbean(new BigDecimal(0.044), new BigDecimal(0.039));
        TPSLbean o22 = new TPSLbean(new BigDecimal(0.046), new BigDecimal(0.040));//0.006
        TPSLbean o23 = new TPSLbean(new BigDecimal(0.048), new BigDecimal(0.042));
        TPSLbean o24 = new TPSLbean(new BigDecimal(0.050), new BigDecimal(0.044));
        TPSLbean o25 = new TPSLbean(new BigDecimal(0.052), new BigDecimal(0.046));
        TPSLbean o26 = new TPSLbean(new BigDecimal(0.054), new BigDecimal(0.048));
        TPSLbean o27 = new TPSLbean(new BigDecimal(0.056), new BigDecimal(0.049));//0.007
        TPSLbean o28 = new TPSLbean(new BigDecimal(0.058), new BigDecimal(0.051));
        TPSLbean o29 = new TPSLbean(new BigDecimal(0.060), new BigDecimal(0.053));
        TPSLbean o30 = new TPSLbean(new BigDecimal(0.062), new BigDecimal(0.055));
        TPSLbean o31 = new TPSLbean(new BigDecimal(0.064), new BigDecimal(0.057));
        TPSLbean o32 = new TPSLbean(new BigDecimal(0.066), new BigDecimal(0.058));//0.008
        TPSLbean o33 = new TPSLbean(new BigDecimal(0.068), new BigDecimal(0.060));
        TPSLbean o34 = new TPSLbean(new BigDecimal(0.070), new BigDecimal(0.062));
        TPSLbean o35 = new TPSLbean(new BigDecimal(0.072), new BigDecimal(0.064));
        TPSLbean o36 = new TPSLbean(new BigDecimal(0.074), new BigDecimal(0.066));
        TPSLbean o37 = new TPSLbean(new BigDecimal(0.076), new BigDecimal(0.067));//0.009
        TPSLbean o38 = new TPSLbean(new BigDecimal(0.078), new BigDecimal(0.069));
        TPSLbean o39 = new TPSLbean(new BigDecimal(0.080), new BigDecimal(0.071));
        TPSLbean o40 = new TPSLbean(new BigDecimal(0.082), new BigDecimal(0.073));
        TPSLbean o41 = new TPSLbean(new BigDecimal(0.084), new BigDecimal(0.075));
        TPSLbean o42 = new TPSLbean(new BigDecimal(0.086), new BigDecimal(0.076));//0.010
        TPSLbean o43 = new TPSLbean(new BigDecimal(0.088), new BigDecimal(0.078));
        TPSLbean o44 = new TPSLbean(new BigDecimal(0.090), new BigDecimal(0.080));
        TPSLbean o45 = new TPSLbean(new BigDecimal(0.092), new BigDecimal(0.082));
        TPSLbean o46 = new TPSLbean(new BigDecimal(0.094), new BigDecimal(0.084));
        TPSLbean o47 = new TPSLbean(new BigDecimal(0.096), new BigDecimal(0.085));//0.011
        TPSLbean o48 = new TPSLbean(new BigDecimal(0.098), new BigDecimal(0.087));
        TPSLbean o49 = new TPSLbean(new BigDecimal(0.100), new BigDecimal(0.089));
        TPSLbean o50 = new TPSLbean(new BigDecimal(0.102), new BigDecimal(0.091));
        TPSLbean o51 = new TPSLbean(new BigDecimal(0.104), new BigDecimal(0.093));
        TPSLbean o52 = new TPSLbean(new BigDecimal(0.106), new BigDecimal(0.094));//0.012
        TPSLbean o53 = new TPSLbean(new BigDecimal(0.108), new BigDecimal(0.096));
        TPSLbean o54 = new TPSLbean(new BigDecimal(0.110), new BigDecimal(0.098));
        TPSLbean o55 = new TPSLbean(new BigDecimal(0.112), new BigDecimal(0.100));
        TPSLbean o56 = new TPSLbean(new BigDecimal(0.114), new BigDecimal(0.102));
        TPSLbean o57 = new TPSLbean(new BigDecimal(0.116), new BigDecimal(0.103));//0.013
        TPSLbean o58 = new TPSLbean(new BigDecimal(0.118), new BigDecimal(0.105));
        TPSLbean o59 = new TPSLbean(new BigDecimal(0.120), new BigDecimal(0.107));
        TPSLbean o60 = new TPSLbean(new BigDecimal(0.122), new BigDecimal(0.109));
        TPSLbean o61 = new TPSLbean(new BigDecimal(0.124), new BigDecimal(0.111));
        TPSLbean o62 = new TPSLbean(new BigDecimal(0.126), new BigDecimal(0.112));//0.014
        TPSLbean o63 = new TPSLbean(new BigDecimal(0.128), new BigDecimal(0.114));
        TPSLbean o64 = new TPSLbean(new BigDecimal(0.130), new BigDecimal(0.116));
        TPSLbean o65 = new TPSLbean(new BigDecimal(0.132), new BigDecimal(0.118));
        TPSLbean o66 = new TPSLbean(new BigDecimal(0.134), new BigDecimal(0.120));
        TPSLbean o67 = new TPSLbean(new BigDecimal(0.136), new BigDecimal(0.121));//0.015
        TPSLbean o68 = new TPSLbean(new BigDecimal(0.138), new BigDecimal(0.123));
        TPSLbean o69 = new TPSLbean(new BigDecimal(0.140), new BigDecimal(0.125));
        TPSLbean o70 = new TPSLbean(new BigDecimal(0.142), new BigDecimal(0.127));
        TPSLbean o71 = new TPSLbean(new BigDecimal(0.144), new BigDecimal(0.129));
        TPSLbean o72 = new TPSLbean(new BigDecimal(0.146), new BigDecimal(0.130));//0.016
        TPSLbean o73 = new TPSLbean(new BigDecimal(0.148), new BigDecimal(0.132));
        TPSLbean o74 = new TPSLbean(new BigDecimal(0.150), new BigDecimal(0.134));
        TPSLbean o75 = new TPSLbean(new BigDecimal(0.152), new BigDecimal(0.136));
        TPSLbean o76 = new TPSLbean(new BigDecimal(0.154), new BigDecimal(0.138));
        TPSLbean o77 = new TPSLbean(new BigDecimal(0.156), new BigDecimal(0.139));//0.017
        TPSLbean o78 = new TPSLbean(new BigDecimal(0.158), new BigDecimal(0.141));
        TPSLbean o79 = new TPSLbean(new BigDecimal(0.160), new BigDecimal(0.143));
        TPSLbean o80 = new TPSLbean(new BigDecimal(0.162), new BigDecimal(0.145));
        TPSLbean o81 = new TPSLbean(new BigDecimal(0.164), new BigDecimal(0.147));
        TPSLbean o82 = new TPSLbean(new BigDecimal(0.166), new BigDecimal(0.148));//0.018
        TPSLbean o83 = new TPSLbean(new BigDecimal(0.168), new BigDecimal(0.150));
        TPSLbean o84 = new TPSLbean(new BigDecimal(0.170), new BigDecimal(0.152));
        TPSLbean o85 = new TPSLbean(new BigDecimal(0.172), new BigDecimal(0.154));
        TPSLbean o86 = new TPSLbean(new BigDecimal(0.174), new BigDecimal(0.156));
        TPSLbean o87 = new TPSLbean(new BigDecimal(0.176), new BigDecimal(0.157));//0.019
        TPSLbean o88 = new TPSLbean(new BigDecimal(0.178), new BigDecimal(0.159));
        TPSLbean o89 = new TPSLbean(new BigDecimal(0.180), new BigDecimal(0.161));
        TPSLbean o90 = new TPSLbean(new BigDecimal(0.182), new BigDecimal(0.163));
        TPSLbean o91 = new TPSLbean(new BigDecimal(0.184), new BigDecimal(0.165));
        TPSLbean o92 = new TPSLbean(new BigDecimal(0.186), new BigDecimal(0.166));//0.020
        TPSLbean o93 = new TPSLbean(new BigDecimal(0.188), new BigDecimal(0.168));
        TPSLbean o94 = new TPSLbean(new BigDecimal(0.190), new BigDecimal(0.170));
        TPSLbean o95 = new TPSLbean(new BigDecimal(0.192), new BigDecimal(0.172));
        TPSLbean o96 = new TPSLbean(new BigDecimal(0.194), new BigDecimal(0.174));
        TPSLbean o97 = new TPSLbean(new BigDecimal(0.196), new BigDecimal(0.175));//0.021
        TPSLbean o98 = new TPSLbean(new BigDecimal(0.198), new BigDecimal(0.177));
        TPSLbean o99 = new TPSLbean(new BigDecimal(0.200), new BigDecimal(0.179));


//        tpSlList.add(o001);
        tpSlList.add(o00);
        tpSlList.add(o01);
        tpSlList.add(o02);
        tpSlList.add(o03);
        tpSlList.add(o04);
        tpSlList.add(o05);
        tpSlList.add(o06);
        tpSlList.add(o07);
        tpSlList.add(o08);
        tpSlList.add(o09);

        tpSlList.add(o10);
        tpSlList.add(o11);
        tpSlList.add(o12);
        tpSlList.add(o13);
        tpSlList.add(o14);
        tpSlList.add(o15);
        tpSlList.add(o16);
        tpSlList.add(o17);
        tpSlList.add(o18);
        tpSlList.add(o19);

        tpSlList.add(o20);
        tpSlList.add(o21);
        tpSlList.add(o22);
        tpSlList.add(o23);
        tpSlList.add(o24);
        tpSlList.add(o25);
        tpSlList.add(o26);
        tpSlList.add(o27);
        tpSlList.add(o28);
        tpSlList.add(o29);


        tpSlList.add(o30);
        tpSlList.add(o31);
        tpSlList.add(o32);
        tpSlList.add(o33);
        tpSlList.add(o34);
        tpSlList.add(o35);
        tpSlList.add(o36);
        tpSlList.add(o37);
        tpSlList.add(o38);
        tpSlList.add(o39);

        tpSlList.add(o40);
        tpSlList.add(o41);
        tpSlList.add(o42);
        tpSlList.add(o43);
        tpSlList.add(o44);
        tpSlList.add(o45);
        tpSlList.add(o46);
        tpSlList.add(o47);
        tpSlList.add(o48);
        tpSlList.add(o49);

        tpSlList.add(o50);
        tpSlList.add(o51);
        tpSlList.add(o52);
        tpSlList.add(o53);
        tpSlList.add(o54);
        tpSlList.add(o55);
        tpSlList.add(o56);
        tpSlList.add(o57);
        tpSlList.add(o58);
        tpSlList.add(o59);

        tpSlList.add(o60);
        tpSlList.add(o61);
        tpSlList.add(o62);
        tpSlList.add(o63);
        tpSlList.add(o64);
        tpSlList.add(o65);
        tpSlList.add(o66);
        tpSlList.add(o67);
        tpSlList.add(o68);
        tpSlList.add(o69);

        tpSlList.add(o70);
        tpSlList.add(o71);
        tpSlList.add(o72);
        tpSlList.add(o73);
        tpSlList.add(o74);
        tpSlList.add(o75);
        tpSlList.add(o76);
        tpSlList.add(o77);
        tpSlList.add(o78);
        tpSlList.add(o79);

        tpSlList.add(o80);
        tpSlList.add(o81);
        tpSlList.add(o82);
        tpSlList.add(o83);
        tpSlList.add(o84);
        tpSlList.add(o85);
        tpSlList.add(o86);
        tpSlList.add(o87);
        tpSlList.add(o88);
        tpSlList.add(o89);

        tpSlList.add(o90);
        tpSlList.add(o91);
        tpSlList.add(o92);
        tpSlList.add(o93);
        tpSlList.add(o94);
        tpSlList.add(o95);
        tpSlList.add(o96);
        tpSlList.add(o97);
        tpSlList.add(o98);
        tpSlList.add(o99);

        return tpSlList;

    }

}
