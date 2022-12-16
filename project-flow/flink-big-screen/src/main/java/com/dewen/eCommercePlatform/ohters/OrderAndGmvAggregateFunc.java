package com.dewen.eCommercePlatform.ohters;

import com.dewen.eCommercePlatform.entity.OrderAccumulator;
import com.dewen.eCommercePlatform.entity.SubOrderDetail;
import org.apache.flink.api.common.functions.AggregateFunction;

/**
 * 聚合函数：根据站点聚合
 */
public final class OrderAndGmvAggregateFunc implements AggregateFunction<SubOrderDetail, OrderAccumulator, OrderAccumulator> {
    private static final long serialVersionUID = 1L;

    @Override
    public OrderAccumulator createAccumulator() {
        return new OrderAccumulator();
    }

    @Override
    public OrderAccumulator add(SubOrderDetail record, OrderAccumulator acc) {
        if (acc.getSiteId() == 0) {
            acc.setSiteId(record.getSiteId());
            acc.setSiteName(record.getSiteName());
        }
        acc.addOrderId(record.getOrderId());
        acc.addSubOrderSum(1);
        acc.addQuantitySum(record.getQuantity());
        acc.addGmv(record.getPrice() * record.getQuantity());
        return acc;
    }

    @Override
    public OrderAccumulator getResult(OrderAccumulator acc) {
        return acc;
    }

    @Override
    public OrderAccumulator merge(OrderAccumulator acc1, OrderAccumulator acc2) {
        if (acc1.getSiteId() == 0) {
            acc1.setSiteId(acc2.getSiteId());
            acc1.setSiteName(acc2.getSiteName());
        }
        acc1.addOrderIds(acc2.getOrderIds());
        acc1.addSubOrderSum(acc2.getSubOrderSum());
        acc1.addQuantitySum(acc2.getQuantitySum());
        acc1.addGmv(acc2.getGmv());
        return acc1;
    }
}
