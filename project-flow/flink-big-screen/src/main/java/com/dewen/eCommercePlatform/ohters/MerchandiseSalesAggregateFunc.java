package com.dewen.eCommercePlatform.ohters;

import com.dewen.eCommercePlatform.entity.SubOrderDetail;
import org.apache.flink.api.common.functions.AggregateFunction;

/**
 * @author dewen
 * @date 2022/12/6 17:15
 */
public class MerchandiseSalesAggregateFunc implements AggregateFunction<SubOrderDetail, Long, Long> {
    @Override
    public Long createAccumulator() {
        return 0L;
    }

    @Override
    public Long add(SubOrderDetail subOrderDetail, Long aLong) {
        return aLong + subOrderDetail.getQuantity();
    }

    @Override
    public Long getResult(Long aLong) {
        return aLong;
    }

    @Override
    public Long merge(Long aLong, Long acc1) {
        return aLong + acc1;
    }
}
