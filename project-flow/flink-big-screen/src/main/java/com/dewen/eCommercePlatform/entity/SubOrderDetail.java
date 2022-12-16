package com.dewen.eCommercePlatform.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dewen
 * @date 2022/12/6 10:43
 */
@Data
public class SubOrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private long userId;
    private long orderId;
    private long subOrderId;
    private long siteId;
    private String siteName;
    private long cityId;
    private String cityName;
    private long warehouseId;
    private long merchandiseId;
    private long price;
    private long quantity;
    private int orderStatus;
    private int isNewOrder;
    private long timestamp;
}
