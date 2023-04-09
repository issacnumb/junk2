package com.bot.thread;

import com.bot.bean.*;
import com.bot.constant.ParamNote;
import com.bot.service.BianService;
import com.bot.service.MessageHandler;
import com.bot.service.TestClass;
import com.bot.service.WeexService;
import com.bot.storage.CommonInfoStorage;
import com.bot.utils.CustomLogUtil;
import com.bot.utils.PriceComputeUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.bot.bean.CloseOrderConst.HOLD_SIDE_LONG;
import static com.bot.bean.PlaceOrderInterface.weexBTCUSDT;
import static com.bot.bean.PlaceOrderInterface.weexETHUSDT;

@Slf4j
@Component
public class CoreThread {

    @Autowired
    public TestClass test;
    @Autowired
    public MessageHandler messageHandler;
    @Autowired
    public ParamNote paramNote;
    @Autowired
    public WeexService weexService;
    @Autowired
    public CommonInfoStorage storage;
    @Autowired
    public BianService bianService;

    @Autowired
    private CustomLogUtil logUtil;

    @Async
    public CompletableFuture<String> work() {

        return CompletableFuture.supplyAsync(() -> {

            log.info("线程启动");
            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < 60 * 1000) {

                try {
                    Thread.sleep(paramNote.getCommonSleepTime());
                } catch (InterruptedException e) {
                    log.error("Common Sleep error!");
                }

                // 开启币安webSocket
                try {
                    bianService.getLastPriceFromBiAn();
                } catch (Exception e) {
                    log.error("BiAn socket error : {}", e.getMessage());
                    log.error("stack trace : {} ", e.getStackTrace());
                }

                if (paramNote.isEthEnable() && storage.getEthLastPrice() == null) {
                    try {
                        log.error("ETH price is not prepare yet...  wait 2 second to retry");
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        log.error("Sleep function error : {}", e.getMessage());
                        log.error("stack trace : {} ", e.getStackTrace());
                    }
                    continue;
                }

                // TODO 暂时关闭BTC价格确认
//            if (paramNote.isBtcEnable() && storage.getBtcLastPrice() == null) {
//                try {
//                    log.error("BTC price is not prepare yet...  wait 2 second to retry");
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    log.error("Sleep function error : {}", e.getMessage());
//                    log.error("stack trace : {} ", e.getStackTrace());
//                }
//                continue;
//            }


                // 定义查询对象 ,eth / btc 通用
                GetCurrentTrackingOrderListBean getCurrentTrackingOrderListBean = new GetCurrentTrackingOrderListBean();

                String updateSign = "updateSign" + System.currentTimeMillis();
                try {
//---------------------------------------------------过滤掉相同订单--------------------------------------------------------
                    List<String> tempContainOpenOrderNoList = new ArrayList<>();
                    for (TpPackageBean itemBean : storage.getCurrentPosition()) {
                        if (tempContainOpenOrderNoList.contains(itemBean.getOpenOrderId())) {
                            storage.getCurrentPosition().remove(itemBean);
                        } else {
                            tempContainOpenOrderNoList.add(itemBean.getOpenOrderId());
                        }
                    }
//-------------------------------------------------------ETH------------------------------------------------------------
                    //生成本次循环 更新标识
                    //查询当前带单
                    //eth 全部带单 临时容器
                    List<GetCurrentTrackingOrderListResponseBean.InnerData> ethTotalTrackingOrderList = new ArrayList<>();
                    for (int page = 1; ; page++) {
                        getCurrentTrackingOrderListBean.setSymbol(weexETHUSDT);
                        getCurrentTrackingOrderListBean.setPageNum(page);
                        getCurrentTrackingOrderListBean.setPageSize(50);
                        GetCurrentTrackingOrderListResponseBean ethCurrentTrackingOrderList = weexService.getCurrentTrackingOrderList(getCurrentTrackingOrderListBean);
                        List<GetCurrentTrackingOrderListResponseBean.InnerData> dataList = ethCurrentTrackingOrderList.getData().getData();
                        logUtil.recordMoreDetail("ETH 查询到当前带单为：" + dataList);
                        if (dataList.size() > 0) {
                            ethTotalTrackingOrderList.addAll(dataList);
                        }
                        if (page * 50 >= ethCurrentTrackingOrderList.getData().getTotal()) {
                            break;
                        }
                    }

                    // TODO 在修改BTC的时候一定注意  如果为空  就不会进入下面的循环，  需要将订单放进去
                    if (storage.getCurrentPosition().size() == 0) {
                        for (int index = 0; index < ethTotalTrackingOrderList.size(); index++) {
                            GetCurrentTrackingOrderListResponseBean.InnerData innerData = ethTotalTrackingOrderList.get(index);
                            TpPackageBean addNewPackageBean = new TpPackageBean();
                            addNewPackageBean.setProductCode(weexETHUSDT);
                            addNewPackageBean.setOpenPrice(innerData.getAverageOpenPrice());
                            addNewPackageBean.setOpenOrderId(innerData.getOpenOrderNo());
                            addNewPackageBean.setOrderNo(innerData.getOrderNo());
                            addNewPackageBean.setIsPositionControl(true);
                            addNewPackageBean.setUpdateSign(updateSign);
                            if (innerData.getHoldSide().equals(HOLD_SIDE_LONG)) {
                                addNewPackageBean.setSide(1);
                            } else {
                                addNewPackageBean.setSide(2);
                            }
                            storage.getCurrentPosition().add(addNewPackageBean);
                            logUtil.recordMoreDetail("eth storage 增加了订单， 此时storage存储的订单为 ： " + storage.getCurrentPosition());
                        }
                    }

                    //将带单信息 同步到storage
                    logUtil.recordMoreDetail("当前storage存储的订单为 ： " + storage.getCurrentPosition());
                    Iterator<TpPackageBean> iterator4Eth = storage.getCurrentPosition().iterator();
                    while (iterator4Eth.hasNext()) {

                        boolean isStorageNotContainTracking = true;
                        TpPackageBean next = iterator4Eth.next();
                        GetCurrentTrackingOrderListResponseBean.InnerData innerData = null;

                        for (int totalIndex = 0; totalIndex < ethTotalTrackingOrderList.size(); totalIndex++) {

                            innerData = ethTotalTrackingOrderList.get(totalIndex);

                            if (next.getOpenOrderId().equals(innerData.getOpenOrderNo())) {
                                next.setOrderNo(innerData.getOrderNo());
                                next.setOpenPrice(innerData.getAverageOpenPrice());
                                next.setProductCode(innerData.getProductCode());
                                next.setUpdateSign(updateSign);
                                isStorageNotContainTracking = false;
                                break;
                            }
                        }

                        // 如果 当前的storage中不包含带单查询结果中的 这个eth订单的话,  加进去. 默认档位控制订单
                        if (isStorageNotContainTracking && innerData != null) {
                            TpPackageBean addNewPackageBean = new TpPackageBean();
                            addNewPackageBean.setProductCode(weexETHUSDT);
                            addNewPackageBean.setOpenPrice(innerData.getAverageOpenPrice());
                            addNewPackageBean.setOpenOrderId(innerData.getOpenOrderNo());
                            addNewPackageBean.setOrderNo(innerData.getOrderNo());
                            addNewPackageBean.setIsPositionControl(true);
                            addNewPackageBean.setUpdateSign(updateSign);
                            if (innerData.getHoldSide().equals(HOLD_SIDE_LONG)) {
                                addNewPackageBean.setSide(1);
                            } else {
                                addNewPackageBean.setSide(2);
                            }
                            storage.getCurrentPosition().add(addNewPackageBean);
                            logUtil.recordMoreDetail("eth storage 增加了订单， 此时storage存储的订单为 ： " + storage.getCurrentPosition());
                        }
                    }

//-------------------------------------------------------BTC------------------------------------------------------------
                    //btc 全部带单 订单临时容器
                    List<GetCurrentTrackingOrderListResponseBean.InnerData> btcTotalTrackingOrderList = new ArrayList<>();

                    // TODO 暂时不去查询BTC的订单了
//                for (int page = 1; ; page++) {
//                    getCurrentTrackingOrderListBean.setSymbol(weexBTCUSDT);
//                    getCurrentTrackingOrderListBean.setPageNum(page);
//                    getCurrentTrackingOrderListBean.setPageSize(50);
//                    GetCurrentTrackingOrderListResponseBean btcCurrentTrackingOrderList = weexService.getCurrentTrackingOrderList(getCurrentTrackingOrderListBean);
//                    List<GetCurrentTrackingOrderListResponseBean.InnerData> dataList = btcCurrentTrackingOrderList.getData().getData();
//                    if (dataList.size() > 0) {
//                        btcTotalTrackingOrderList.addAll(dataList);
//                    }
//                    if (page * 50 >= btcCurrentTrackingOrderList.getData().getTotal()) {
//                        break;
//                    }
//                }

                    // TODO 在修改BTC的时候一定注意  如果为空  就不会进入下面的循环，  需要将订单放进去
                    if (storage.getCurrentPosition().size() == 0) {
                        for (int index = 0; index < btcTotalTrackingOrderList.size(); index++) {
                            GetCurrentTrackingOrderListResponseBean.InnerData innerData = btcTotalTrackingOrderList.get(index);
                            TpPackageBean addNewPackageBean = new TpPackageBean();
                            addNewPackageBean.setProductCode(weexBTCUSDT);
                            addNewPackageBean.setOpenPrice(innerData.getAverageOpenPrice());
                            addNewPackageBean.setOpenOrderId(innerData.getOpenOrderNo());
                            addNewPackageBean.setOrderNo(innerData.getOrderNo());
                            addNewPackageBean.setIsPositionControl(true);
                            addNewPackageBean.setUpdateSign(updateSign);
                            if (innerData.getHoldSide().equals(HOLD_SIDE_LONG)) {
                                addNewPackageBean.setSide(1);
                            } else {
                                addNewPackageBean.setSide(2);
                            }
                            storage.getCurrentPosition().add(addNewPackageBean);
                            logUtil.recordMoreDetail("eth storage 增加了订单， 此时storage存储的订单为 ： " + storage.getCurrentPosition());
                        }
                    }

                    //将订单信息同步到storage
                    Iterator<TpPackageBean> iterator4Btc = storage.getCurrentPosition().iterator();
                    while (iterator4Btc.hasNext()) {

                        boolean isStorageNotContainTracking = true;
                        TpPackageBean next = iterator4Btc.next();
                        GetCurrentTrackingOrderListResponseBean.InnerData innerData = null;

                        for (int totalIndex = 0; totalIndex < btcTotalTrackingOrderList.size(); totalIndex++) {

                            innerData = btcTotalTrackingOrderList.get(totalIndex);

                            if (next.getOpenOrderId().equals(innerData.getOpenOrderNo())) {
                                next.setOrderNo(innerData.getOrderNo());
                                next.setOpenPrice(innerData.getAverageOpenPrice());
                                next.setProductCode(innerData.getProductCode());
                                next.setUpdateSign(updateSign);
                                isStorageNotContainTracking = false;
                                break;
                            }
                        }

                        // 如果 当前的storage中不包含带单查询结果中的 这个btc订单的话,  加进去. 默认档位订单
                        if (isStorageNotContainTracking && innerData != null) {
                            TpPackageBean addNewPackageBean = new TpPackageBean();
                            addNewPackageBean.setProductCode(weexBTCUSDT);
                            addNewPackageBean.setOpenPrice(innerData.getAverageOpenPrice());
                            addNewPackageBean.setOpenOrderId(innerData.getOpenOrderNo());
                            addNewPackageBean.setOrderNo(innerData.getOrderNo());
                            addNewPackageBean.setUpdateSign(updateSign);
                            addNewPackageBean.setIsPositionControl(true);
                            if (innerData.getHoldSide().equals(HOLD_SIDE_LONG)) {
                                addNewPackageBean.setSide(1);
                            } else {
                                addNewPackageBean.setSide(2);
                            }
                            storage.getCurrentPosition().add(addNewPackageBean);
                        }
                    }

                } catch (Exception e) {
                    log.error("update positions throw error : {}", e);
                    log.error("stack trace : {} ", e.getStackTrace());
                }

                //将交易不成功:  产品代码为空  / openOrderNo / orderNo为空 / 开仓价格为空的 数据剔除

                for (TpPackageBean itemBean : storage.getCurrentPosition()) {
                    if (itemBean.getOrderNo() == null || itemBean.getOpenPrice() == null || itemBean.getProductCode() == null || itemBean.getSide() == null || !(itemBean.getUpdateSign().equals(updateSign))) {
                        Integer newtDisusePoint = itemBean.getDisusePoint() + 1;
                        if (newtDisusePoint > 99) {
                            storage.getCurrentPosition().remove(itemBean);
                        } else {
                            itemBean.setDisusePoint(newtDisusePoint);
                        }
                    }
                }

                // CopyOnWriteArrayList的迭代器 不支持remove
//            Iterator<TpPackageBean> iterator4All = storage.getCurrentPosition().iterator();
//            while (iterator4All.hasNext()) {
//                TpPackageBean tempPackageBean = iterator4All.next();
//                if (tempPackageBean.getOrderNo() == null || tempPackageBean.getOpenPrice() == null || tempPackageBean.getProductCode() == null || tempPackageBean.getSide() == null || !(tempPackageBean.getUpdateSign().equals(updateSign))) {
//                    Integer newtDisusePoint = tempPackageBean.getDisusePoint() + 1;
//                    if (newtDisusePoint > 99) {
//                        iterator4All.remove();
//                    } else {
//                        tempPackageBean.setDisusePoint(newtDisusePoint);
//                    }
//                }
//            }


//----------------------------------------------ETH过滤需要平仓的订单号-----------------------------------------------------
                CopyOnWriteArrayList<TpPackageBean> positionList = storage.getCurrentPosition();
                ArrayList<TPSLbean> tpSlBeanList = storage.getTpSlBeanList();
                List<String> ethCloseNos = null;
                List<String> btcCloseNos = null;

                for (TpPackageBean position : positionList) {

                    BigDecimal openPrice = position.getOpenPrice();

                    if (position.getOrderNo() == null || position.getOpenPrice() == null) {
                        continue;
                    }

                    if (position.getProductCode().equals(weexETHUSDT)) {

                        BigDecimal currentEthPrice = storage.getEthLastPrice();
                        if (currentEthPrice == null) continue;

                        if (position.getSide() == 1) {
                            // 以太坊多单
                            // 普通单 档位控制单 公用止损价格
                            BigDecimal slPrice = openPrice.multiply(BigDecimal.ONE.subtract(paramNote.getEthSL()));
                            // 判断是否止损
                            logUtil.recordMoreDetail("进入以太坊多单判断");
                            if (currentEthPrice.compareTo(slPrice) < 1) {
                                log.info("以太坊多单止损,开启止损流程 止损价格 ： " + slPrice);
                                if (ethCloseNos == null) {
                                    ethCloseNos = new ArrayList<String>();
                                }
                                ethCloseNos.add(position.getOrderNo());
                                positionList.remove(position);
                                continue;
                            }
                            logUtil.recordMoreDetail("以太坊多单没有止损,进入判断止盈流程");

                            // 判断是否止盈
                            if (position.getIsPositionControl()) {
                                // 档位控制单
                                logUtil.recordMoreDetail("以太坊多单 进入档位控制单分支");
                                if (position.getTpSlIndex() == null) {
                                    // 没有进入档位
                                    logUtil.recordMoreDetail("以太坊多单 之前没有进入档位,  判断是否进入档位");

                                    BigDecimal index_0_price = PriceComputeUtil.computeWinPrice(position.getOpenPrice(), tpSlBeanList.get(0).getTpPoint(), 1);

                                    //处于盈利阶段 但是小于最低档位
                                    if (currentEthPrice.compareTo(index_0_price) < 0) {
                                        logUtil.recordMoreDetail("以太坊多单 价格小于 第0档位价格  终止流程--------------------------");
                                        continue;
                                    }

                                    logUtil.recordMoreDetail("以太坊多单 大于第0档位价格  开始判断档位");
                                    for (int index = 0; index < tpSlBeanList.size(); index++) {
                                        //超过了最多
                                        if (index + 1 == tpSlBeanList.size()) {
                                            position.setTpSlIndex(tpSlBeanList.size() - 1);
                                            continue;
                                        }

                                        TPSLbean currentTpBean = tpSlBeanList.get(index);
                                        TPSLbean nextTpBean = tpSlBeanList.get(index + 1);

                                        BigDecimal currentIndexPrice = PriceComputeUtil.computeWinPrice(position.getOpenPrice(), currentTpBean.getTpPoint(), 1);
                                        BigDecimal nextIndexPrice = PriceComputeUtil.computeWinPrice(position.getOpenPrice(), nextTpBean.getTpPoint(), 1);
                                        logUtil.recordMoreDetail("以太坊多单 判断是否进入第 " + index + " 档位，当前档位价格：" + currentIndexPrice + "下一档位价格：" + nextIndexPrice);
                                        if (currentEthPrice.compareTo(currentIndexPrice) > -1 && currentEthPrice.compareTo(nextIndexPrice) < 0) {
                                            log.info("以太坊多单 成功进入第 ： " + index + " 档位");
                                            position.setTpSlIndex(index);
                                            break;
                                        }
                                    }
                                    continue;
                                } else {
                                    // 进入了档位
                                    // 首选检查是否达到档位止损
                                    logUtil.recordMoreDetail("以太坊多单 之前之前已经进入档位,  进入判断分支");
                                    TPSLbean currentBean = tpSlBeanList.get(position.getTpSlIndex());
                                    BigDecimal ethControlSlPrice = PriceComputeUtil.computeWinPrice(position.getOpenPrice(), currentBean.getSlPoint(), 1);
                                    logUtil.recordMoreDetail("以太坊多单处于第 " + position.getTpSlIndex() + " 档位 ，  开始判断是否达到档位止损， 档位止损价格 ： " + ethControlSlPrice);
                                    if (currentEthPrice.compareTo(ethControlSlPrice) < 1) {
                                        log.info("以太坊多单处于第 " + position.getTpSlIndex() + " 档位 ，  达到档位止损， 档位止损价格 ： " + ethControlSlPrice);
                                        if (ethCloseNos == null) {
                                            ethCloseNos = new ArrayList<>();
                                        }
                                        positionList.remove(position);
                                        ethCloseNos.add(position.getOrderNo());
                                        continue;
                                    }

                                    // 然后检查是否调高档位
                                    // 如果当前价格 小于 小于下一个档位 , 则保持不变 什么都不用做  考虑下一单
                                    if (position.getTpSlIndex() + 1 >= tpSlBeanList.size()) {
                                        //已经是最大档位了,  别调了
                                        continue;
                                    }

                                    // 判断是否达到下一个档位
                                    TPSLbean nextBean = tpSlBeanList.get(position.getTpSlIndex() + 1);
                                    BigDecimal ethControlTpPrice = PriceComputeUtil.computeWinPrice(position.getOpenPrice(), nextBean.getTpPoint(), 1);
                                    logUtil.recordMoreDetail("以太坊多单 判断是否达到下一档位价格 ： " + ethControlTpPrice);
                                    if (currentEthPrice.compareTo(ethControlTpPrice) < 0) {
                                        logUtil.recordMoreDetail("以太坊多单 小于下一档位价格 ： " + ethControlTpPrice + ", 不再进行档位计算");
                                        continue;
                                    }

                                    // 前边的流程都走完了,  只有提高档位这么一个选择
                                    logUtil.recordMoreDetail("以太坊多单 开始计算提高档位 ");
                                    for (int improveIndex = position.getTpSlIndex(); improveIndex < tpSlBeanList.size(); improveIndex++) {

                                        //超过了最高档位
                                        if (improveIndex + 1 == tpSlBeanList.size()) {
                                            position.setTpSlIndex(tpSlBeanList.size() - 1);
                                            break;
                                        }

                                        BigDecimal ethImproveCurrentPrice = PriceComputeUtil.computeWinPrice(position.getOpenPrice(), tpSlBeanList.get(improveIndex).getTpPoint(), 1);
                                        BigDecimal ethImproveNextPrice = PriceComputeUtil.computeWinPrice(position.getOpenPrice(), tpSlBeanList.get(improveIndex + 1).getTpPoint(), 1);

                                        if (currentEthPrice.compareTo(ethImproveCurrentPrice) > -1 && currentEthPrice.compareTo(ethImproveNextPrice) < 0) {
                                            log.info("以太坊多单 更新档位到：" + improveIndex + ",  该档位价格 ： " + ethImproveCurrentPrice);
                                            position.setTpSlIndex(improveIndex);
                                            break;
                                        }
                                    }
                                    continue;
                                }

                            } else {
                                // 普通单
                                logUtil.recordMoreDetail("以太坊多单 进入   普通单   分支");
                                BigDecimal tpPrice = openPrice.multiply(BigDecimal.ONE.add(paramNote.getEthTP()));
                                if (currentEthPrice.compareTo(tpPrice) > -1) {
                                    if (ethCloseNos == null) {
                                        ethCloseNos = new ArrayList<String>();
                                    }
                                    log.info("以太坊多单-普通单-止盈，止盈价格 ： " + tpPrice);
                                    ethCloseNos.add(position.getOrderNo());
                                    positionList.remove(position);
                                    continue;
                                }
                            }


                        } else if (position.getSide().equals(2)) {
                            // 以太坊空单
                            // 普通单 档位控制单 公用止损价格
                            BigDecimal slPrice = openPrice.multiply(BigDecimal.ONE.add(paramNote.getEthSL()));
                            // 判断是否止损
                            logUtil.recordMoreDetail("进入以太坊空单判断");
                            if (currentEthPrice.compareTo(slPrice) > -1) {
                                log.info("以太坊空单止损,开启止损流程 止损价格 ： " + slPrice);
                                if (ethCloseNos == null) {
                                    ethCloseNos = new ArrayList<String>();
                                }
                                ethCloseNos.add(position.getOrderNo());
                                positionList.remove(position);
                                continue;
                            }
                            logUtil.recordMoreDetail("以太坊空单没有止损,进入判断止盈流程");

                            // 判断是否止盈
                            if (position.getIsPositionControl()) {
                                // 档位控制单
                                logUtil.recordMoreDetail("以太坊空单 进入档位控制单分支");
                                if (position.getTpSlIndex() == null) {
                                    // 没有进入档位
                                    logUtil.recordMoreDetail("以太坊空单 之前没有进入档位,  判断是否进入档位");

                                    BigDecimal index_0_price = PriceComputeUtil.computeWinPrice(position.getOpenPrice(), tpSlBeanList.get(0).getTpPoint(), 2);

                                    //处于盈利阶段 但是 高于 空单最低档位价格
                                    if (currentEthPrice.compareTo(index_0_price) > 0) {
                                        logUtil.recordMoreDetail("以太坊空单 价格高于 第0档位价格  终止流程--------------------------");
                                        continue;
                                    }

                                    logUtil.recordMoreDetail("以太坊空单 小于第0档位价格  开始判断档位");
                                    for (int index = 0; index < tpSlBeanList.size(); index++) {
                                        //超过了最多
                                        if (index + 1 == tpSlBeanList.size()) {
                                            position.setTpSlIndex(tpSlBeanList.size() - 1);
                                            continue;
                                        }

                                        TPSLbean currentTpBean = tpSlBeanList.get(index);
                                        TPSLbean nextTpBean = tpSlBeanList.get(index + 1);

                                        BigDecimal currentIndexPrice = PriceComputeUtil.computeWinPrice(position.getOpenPrice(), currentTpBean.getTpPoint(), 2);
                                        BigDecimal nextIndexPrice = PriceComputeUtil.computeWinPrice(position.getOpenPrice(), nextTpBean.getTpPoint(), 2);
                                        logUtil.recordMoreDetail("以太坊空单 判断是否进入第 " + index + " 档位，当前档位价格：" + currentIndexPrice + "下一档位价格：" + nextIndexPrice);
                                        if (currentEthPrice.compareTo(currentIndexPrice) < 1 && currentEthPrice.compareTo(nextIndexPrice) > 0) {
                                            logUtil.recordMoreDetail("以太坊空单 成功进入第 ： " + index + " 档位");
                                            position.setTpSlIndex(index);
                                            break;
                                        }
                                    }
                                    continue;
                                } else {
                                    // 进入了档位
                                    // 首选检查是否达到档位止损
                                    logUtil.recordMoreDetail("以太坊空单 之前之前已经进入档位,  进入判断分支");
                                    TPSLbean currentBean = tpSlBeanList.get(position.getTpSlIndex());
                                    BigDecimal ethControlSlPrice = PriceComputeUtil.computeWinPrice(position.getOpenPrice(), currentBean.getSlPoint(), 2);
                                    logUtil.recordMoreDetail("以太坊空单处于第 " + position.getTpSlIndex() + " 档位 ， 开始判断是否达到档位止损， 档位止损价格 ： " + ethControlSlPrice);
                                    if (currentEthPrice.compareTo(ethControlSlPrice) > -1) {
                                        log.info("以太坊空单处于第 " + position.getTpSlIndex() + " 档位 ，  达到档位止损， 档位止损价格 ： " + ethControlSlPrice);
                                        if (ethCloseNos == null) {
                                            ethCloseNos = new ArrayList<String>();
                                        }
                                        ethCloseNos.add(position.getOrderNo());
                                        positionList.remove(position);
                                        continue;
                                    }

                                    // 然后检查是否调高档位
                                    // 如果当前价格 小于 小于下一个档位 , 则保持不变 什么都不用做  考虑下一单
                                    if (position.getTpSlIndex() + 1 >= tpSlBeanList.size()) {
                                        //已经是最大档位了,  别调了
                                        continue;
                                    }
                                    // 判断 是否达到下一个档位
                                    TPSLbean nextBean = tpSlBeanList.get(position.getTpSlIndex() + 1);
                                    BigDecimal ethControlTpPrice = PriceComputeUtil.computeWinPrice(position.getOpenPrice(), nextBean.getTpPoint(), 2);
                                    logUtil.recordMoreDetail("以太坊空单 判断是否达到下一档位价格 ： " + ethControlTpPrice);
                                    if (currentEthPrice.compareTo(ethControlTpPrice) > 0) {
                                        logUtil.recordMoreDetail("以太坊空单 大于下一档位价格 ： " + ethControlTpPrice + ", 不再进行档位计算");
                                        continue;
                                    }

                                    // 前边的流程都走完了,  只有提高档位这么一个选择
                                    logUtil.recordMoreDetail("以太坊空单 开始计算提高档位 ");
                                    for (int improveIndex = position.getTpSlIndex(); improveIndex < tpSlBeanList.size(); improveIndex++) {

                                        //超过了最高档位
                                        if (improveIndex + 1 == tpSlBeanList.size()) {
                                            position.setTpSlIndex(tpSlBeanList.size() - 1);
                                            break;
                                        }

                                        BigDecimal ethImproveCurrentPrice = PriceComputeUtil.computeWinPrice(position.getOpenPrice(), tpSlBeanList.get(improveIndex).getTpPoint(), 2);
                                        BigDecimal ethImproveNextPrice = PriceComputeUtil.computeWinPrice(position.getOpenPrice(), tpSlBeanList.get(improveIndex + 1).getTpPoint(), 2);

                                        if (currentEthPrice.compareTo(ethImproveCurrentPrice) < 1 && currentEthPrice.compareTo(ethImproveNextPrice) > 0) {
                                            log.info("以太坊空单 更新档位到：" + improveIndex + ",  该档位价格 ： " + ethImproveCurrentPrice);
                                            position.setTpSlIndex(improveIndex);
                                            break;
                                        }
                                    }
                                    continue;
                                }

                            } else {
                                // 普通单
                                logUtil.recordMoreDetail("以太坊空单 进入   普通单   分支");
                                BigDecimal tpPrice = openPrice.multiply(BigDecimal.ONE.subtract(paramNote.getEthTP()));
                                if (currentEthPrice.compareTo(tpPrice) < 1) {
                                    if (ethCloseNos == null) {
                                        ethCloseNos = new ArrayList<String>();
                                    }
                                    log.info("以太坊空单-普通单-止盈，止盈价格 ： " + tpPrice);
                                    ethCloseNos.add(position.getOrderNo());
                                    positionList.remove(position);
                                    continue;
                                }
                            }

                        }

//----------------------------------------------BTC过滤需要平仓的订单号-----------------------------------------------------
                    } //else if (position.getProductCode().equals(weexBTCUSDT)) {

//                    BigDecimal currentBtcPrice = storage.getBtcLastPrice();
//                    if (currentBtcPrice == null) continue;
//
//                    if (position.getHoldSide().equals(HOLD_SIDE_LONG)) {
//
//                        BigDecimal tpPrice = openPrice.multiply(BigDecimal.ONE.add(paramNote.getBtcTP()));
//                        BigDecimal slPrice = openPrice.multiply(BigDecimal.ONE.subtract(paramNote.getBtcSL()));
//                        if (currentBtcPrice.compareTo(tpPrice) > -1 || currentBtcPrice.compareTo(slPrice) < 1) {
//                            if (btcCloseNos == null) {
//                                btcCloseNos = new ArrayList<String>();
//                            }
//                            btcCloseNos.add(position.getOrderNo());
//                            continue;
//                        }
//
//                    } else if (position.getHoldSide().equals(HOLD_SIDE_SHORT)) {
//
//                        BigDecimal tpPrice = openPrice.multiply(BigDecimal.ONE.subtract(paramNote.getBtcTP()));
//                        BigDecimal slPrice = openPrice.multiply(BigDecimal.ONE.add(paramNote.getBtcSL()));
//                        if (currentBtcPrice.compareTo(tpPrice) < 1 || currentBtcPrice.compareTo(slPrice) > -1) {
//                            if (btcCloseNos == null) {
//                                btcCloseNos = new ArrayList<String>();
//                            }
//                            btcCloseNos.add(position.getOrderNo());
//                            continue;
//                        }
//                    }
//
//                } else {
//                    continue;
//                }
                }

//---------------------------------------------------平仓处理ETH----------------------------------------------------------

                try {
                    if (ethCloseNos != null && ethCloseNos.size() > 0) {

                        List<List<String>> ethPartitionList = Lists.partition(ethCloseNos, 10);

                        for (int ethPartitionListIndex = 0; ethPartitionListIndex < ethPartitionList.size(); ethPartitionListIndex++) {

                            CloseOrderBean closeOrderBean = new CloseOrderBean();
                            closeOrderBean.setSymbol(weexETHUSDT);
                            closeOrderBean.setTrackingNos(ethPartitionList.get(ethPartitionListIndex));

                            log.info("ETH 订单平仓, 平仓价格 : {}", storage.getEthLastPrice());

                            weexService.closeOrder(closeOrderBean);
                        }

                    }

                } catch (Exception e) {
                    log.error(" eth close order ERROR : {}", e.getMessage());
                    log.error(" eth close order ERROR stack trace : {}", e.getStackTrace());
                }

//---------------------------------------------------平仓处理BTC----------------------------------------------------------

                try {
                    if (btcCloseNos != null && btcCloseNos.size() > 0) {

                        List<List<String>> btcPartitionList = Lists.partition(btcCloseNos, 10);

                        for (int btcPartitionListIndex = 0; btcPartitionListIndex < btcPartitionList.size(); btcPartitionListIndex++) {

                            CloseOrderBean closeOrderBean = new CloseOrderBean();
                            closeOrderBean.setSymbol(weexBTCUSDT);
                            closeOrderBean.setTrackingNos(btcPartitionList.get(btcPartitionListIndex));

                            log.info("BTC 订单平仓, 平仓价格 : {}", storage.getBtcLastPrice());

                            weexService.closeOrder(closeOrderBean);
                        }

                    }
                } catch (Exception e) {
                    log.error(" btc close order ERROR : {}", e.getMessage());
                    log.error(" btc close order ERROR stack trace : {}", e.getStackTrace());
                }

                //消息处理
                while (true) {
                    String message = messageHandler.takeMessage();
                    if (message == null) {
                        break;
                    }

                    log.info("接收到指令为 : {}", message);

                    String[] messageArray = message.split(":");
                    if (messageArray.length != 3) {
                        continue;
                    }

//-------------------------------------------------wisdom_price处理------------------------------------------------------

                    if (messageArray[1].equals("wisdom_price")) {

                        log.info("接收到 wisdom_price 信息: {}", message);

                        if (messageArray[0].equals("ETH") || messageArray[0].equals("eth")) {
                            storage.setEthPriceFromWisdom(new BigDecimal(messageArray[2]));
                            storage.setEthWisdomUpdateTime(System.currentTimeMillis());
                            continue;
                        }

                        if (messageArray[0].equals("BTC") || messageArray[0].equals("btc")) {
                            storage.setBtcPriceFromWisdom(new BigDecimal(messageArray[2]));
                            storage.setBtcWisdomUpdateTime(System.currentTimeMillis());
                            continue;
                        }
                    }

//-----------------------------------------------------ETH消息-----------------------------------------------------------

                    if (paramNote.isEthEnable() && (messageArray[0].equals("ETH") || messageArray[0].equals("eth"))) {


                        //做多
                        if (paramNote.isEthOrderLongEnable() && (messageArray[1].equals("long") || messageArray[1].equals("Long"))) {

                            log.info("接收到 ETH 做多 信息: {}", message);
                            log.info("当前 ETH 做多价格: {}", storage.getEthLastPrice());

                            //------------------------------------------普通单-----------------------------------------------

                            PlaceOrderBean ethLongPlaceOrderBean = new PlaceOrderBean();
                            ethLongPlaceOrderBean.setSymbol(weexETHUSDT);
                            ethLongPlaceOrderBean.setSize(String.valueOf(paramNote.getEthOrderAmount() / 2));
                            ethLongPlaceOrderBean.setType(String.valueOf(1));
                            ethLongPlaceOrderBean.setOrder_type(String.valueOf(2));
                            ethLongPlaceOrderBean.setMatch_price(String.valueOf(1));
                            ethLongPlaceOrderBean.setClient_oid(weexETHUSDT + System.currentTimeMillis());

                            try {
                                PlaceOrderResponseBean placeOrderResponseBean = weexService.placeOrder(ethLongPlaceOrderBean);
                                if (placeOrderResponseBean.getOrder_id() == null || placeOrderResponseBean.getClient_oid() == null) {
                                    throw new RuntimeException("返回的 Order_id 为 null 或者 Client_oid 为空");
                                }

                                TpPackageBean ethNormalPackageBean = new TpPackageBean();
                                ethNormalPackageBean.setProductCode(weexETHUSDT);
                                ethNormalPackageBean.setClient_oid(placeOrderResponseBean.getClient_oid());
                                ethNormalPackageBean.setOpenOrderId(placeOrderResponseBean.getOrder_id());
                                ethNormalPackageBean.setSide(1);
                                //false-普通单
                                ethNormalPackageBean.setIsPositionControl(false);

                                storage.getCurrentPosition().add(ethNormalPackageBean);

                            } catch (Exception e) {
                                log.error("ETH 普通单做多出错... message : {}", e.getMessage());
                                log.error("ETH 普通单做多出错... stack : {}", e.getStackTrace());
                            }

                            //-----------------------------------------档位控制单---------------------------------------------

                            //只需要重新定义客户端id即可, 利用同一个对象 节省空间 提高效率
                            ethLongPlaceOrderBean.setClient_oid(weexETHUSDT + System.currentTimeMillis());

                            try {
                                PlaceOrderResponseBean placeControlOrderResponseBean = weexService.placeOrder(ethLongPlaceOrderBean);
                                if (placeControlOrderResponseBean.getOrder_id() == null || placeControlOrderResponseBean.getClient_oid() == null) {
                                    throw new RuntimeException("返回的 Order_id 为 null 或者 Client_oid 为空");
                                }

                                TpPackageBean ethControlPackageBean = new TpPackageBean();
                                ethControlPackageBean.setProductCode(weexETHUSDT);
                                ethControlPackageBean.setClient_oid(placeControlOrderResponseBean.getClient_oid());
                                ethControlPackageBean.setOpenOrderId(placeControlOrderResponseBean.getOrder_id());
                                ethControlPackageBean.setSide(1);
                                //true-档位控制单
                                ethControlPackageBean.setIsPositionControl(true);

                                storage.getCurrentPosition().add(ethControlPackageBean);

                            } catch (Exception e) {
                                log.error("ETH 档位控制单做多出错... message : {}", e.getMessage());
                                log.error("ETH 档位控制单做多出错... stack : {}", e.getStackTrace());
                            }

                            continue;

                        }
                        //做空
                        if (paramNote.isEthOrderShortEnable() && (messageArray[1].equals("short") || messageArray[1].equals("Short"))) {

                            log.info("接收到 ETH 做空 信息: {}", message);
                            log.info("当前 ETH 做空价格: {}", storage.getEthLastPrice());

                            //------------------------------------------普通单-----------------------------------------------

                            PlaceOrderBean ethShortPlaceOrderBean = new PlaceOrderBean();
                            ethShortPlaceOrderBean.setSymbol(weexETHUSDT);
                            ethShortPlaceOrderBean.setSize(String.valueOf(paramNote.getEthOrderAmount() / 2));
                            ethShortPlaceOrderBean.setType(String.valueOf(2));
                            ethShortPlaceOrderBean.setOrder_type(String.valueOf(2));
                            ethShortPlaceOrderBean.setMatch_price(String.valueOf(1));
                            ethShortPlaceOrderBean.setClient_oid(weexETHUSDT + System.currentTimeMillis());

                            try {
                                PlaceOrderResponseBean placeShortOrderResponseBean = weexService.placeOrder(ethShortPlaceOrderBean);
                                if (placeShortOrderResponseBean.getOrder_id() == null || placeShortOrderResponseBean.getClient_oid() == null) {
                                    throw new RuntimeException("返回的 Order_id 为 null 或者 Client_oid 为空");
                                }

                                TpPackageBean ethNormalShortPackageBean = new TpPackageBean();
                                ethNormalShortPackageBean.setProductCode(weexETHUSDT);
                                ethNormalShortPackageBean.setClient_oid(placeShortOrderResponseBean.getClient_oid());
                                ethNormalShortPackageBean.setOpenOrderId(placeShortOrderResponseBean.getOrder_id());
                                ethNormalShortPackageBean.setSide(2);
                                //false-普通单
                                ethNormalShortPackageBean.setIsPositionControl(false);

                                storage.getCurrentPosition().add(ethNormalShortPackageBean);

                            } catch (Exception e) {
                                log.error("ETH 普通单做空出错... message : {}", e.getMessage());
                                log.error("ETH 普通单做空出错... stack : {}", e.getStackTrace());
                            }

                            //-----------------------------------------档位控制单---------------------------------------------

                            ethShortPlaceOrderBean.setClient_oid(weexETHUSDT + System.currentTimeMillis());

                            try {
                                PlaceOrderResponseBean placeShortOrderResponseBean = weexService.placeOrder(ethShortPlaceOrderBean);
                                if (placeShortOrderResponseBean.getOrder_id() == null || placeShortOrderResponseBean.getClient_oid() == null) {
                                    throw new RuntimeException("返回的 Order_id 为 null 或者 Client_oid 为空");
                                }

                                TpPackageBean ethControlShortPackageBean = new TpPackageBean();
                                ethControlShortPackageBean.setProductCode(weexETHUSDT);
                                ethControlShortPackageBean.setClient_oid(placeShortOrderResponseBean.getClient_oid());
                                ethControlShortPackageBean.setOpenOrderId(placeShortOrderResponseBean.getOrder_id());
                                ethControlShortPackageBean.setSide(2);
                                //true-档位控制单
                                ethControlShortPackageBean.setIsPositionControl(true);

                                storage.getCurrentPosition().add(ethControlShortPackageBean);

                            } catch (Exception e) {
                                log.error("ETH 档位控制单做空出错... message : {}", e.getMessage());
                                log.error("ETH 档位控制单做空出错... stack : {}", e.getStackTrace());
                            }

                            continue;

                        }
                    }

//-----------------------------------------------------BTC消息-----------------------------------------------------------

                    if (paramNote.isBtcEnable() && (messageArray[0].equals("BTC") || messageArray[0].equals("btc"))) {
                        //做多// TODO 需要修改 普通单和档位控制单 参照ETH
                        if (paramNote.isBtcOrderLongEnable() && (messageArray[1].equals("long") || messageArray[1].equals("Long"))) {

                            PlaceOrderBean btcLongPlaceOrderBean = new PlaceOrderBean();
                            btcLongPlaceOrderBean.setSymbol(weexBTCUSDT);
                            btcLongPlaceOrderBean.setSize(String.valueOf(paramNote.getEthOrderAmount()));
                            btcLongPlaceOrderBean.setType(String.valueOf(1));
                            btcLongPlaceOrderBean.setOrder_type(String.valueOf(2));
                            btcLongPlaceOrderBean.setMatch_price(String.valueOf(1));
                            btcLongPlaceOrderBean.setClient_oid(weexBTCUSDT + System.currentTimeMillis());

                            log.info("接收到 BTC 做多 信息: {}", message);
                            log.info("当前 BTC 做多价格: {}", storage.getBtcLastPrice());

                            weexService.placeOrder(btcLongPlaceOrderBean);

                            continue;

                        }
                        //做空// TODO 需要修改 普通单和档位控制单 参照ETH
                        if (paramNote.isBtcOrderShortEnable() && (messageArray[1].equals("short") || messageArray[1].equals("Short"))) {

                            PlaceOrderBean btcShortPlaceOrderBean = new PlaceOrderBean();
                            btcShortPlaceOrderBean.setSymbol(weexBTCUSDT);
                            btcShortPlaceOrderBean.setSize(String.valueOf(paramNote.getEthOrderAmount()));
                            btcShortPlaceOrderBean.setType(String.valueOf(2));
                            btcShortPlaceOrderBean.setOrder_type(String.valueOf(2));
                            btcShortPlaceOrderBean.setMatch_price(String.valueOf(1));
                            btcShortPlaceOrderBean.setClient_oid(weexBTCUSDT + System.currentTimeMillis());

                            log.info("接收到 BTC 做空 信息: {}", message);
                            log.info("当前 BTC 做空价格: {}", storage.getBtcLastPrice());

                            weexService.placeOrder(btcShortPlaceOrderBean);

                            continue;

                        }
                    }
                }
            }

            log.info("线程停止");

            return "Task Result";
        });


    }

}
