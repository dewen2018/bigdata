package com.dewen.eCommercePlatform.entity;

import lombok.Data;

import java.util.HashSet;

/**
 * @author dewen
 * @date 2022/12/6 10:53
 */
@Data
public class OrderAccumulator {
    private long siteId;
    private String siteName;
    // 子订单数
    private long subOrderSum;
    // 总额
    private long gmv;
    // 数量总和
    private long quantitySum;
    private HashSet<Long> orderIds;


    public void addSubOrderSum(long i) {
        this.subOrderSum += i;
    }

    public void addQuantitySum(long quantity) {
        this.quantitySum += quantity;
    }

    public void addGmv(long l) {
        this.gmv += l;
    }

    public void addOrderIds(HashSet<Long> orderIds) {
        this.orderIds.addAll(orderIds);
    }

    public void addOrderId(long orderId) {
        this.orderIds.add(orderId);
    }
}
