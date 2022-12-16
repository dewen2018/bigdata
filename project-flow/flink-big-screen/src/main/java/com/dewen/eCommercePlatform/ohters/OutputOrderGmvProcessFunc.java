package com.dewen.eCommercePlatform.ohters;

import com.alibaba.fastjson.JSONObject;
import com.dewen.eCommercePlatform.entity.OrderAccumulator;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * 处理
 */
public final class OutputOrderGmvProcessFunc extends KeyedProcessFunction<Tuple, OrderAccumulator, Tuple2<Long, String>> {
    private static final long serialVersionUID = 1L;

    /**
     * 减少写出：
     * 用一个MapState状态缓存当前所有站点的聚合数据。
     * 由于数据源是以子订单为单位的，因此如果站点ID在MapState中没有缓存，
     * 或者缓存的子订单数与当前子订单数不一致，表示结果有更新，这样的数据才允许输出。
     */
    private MapState<Long, OrderAccumulator> state;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        state = this.getRuntimeContext().getMapState(new MapStateDescriptor<>(
                "state_site_order_gmv",
                Long.class,
                OrderAccumulator.class)
        );
    }

    @Override
    public void processElement(OrderAccumulator value, Context ctx, Collector<Tuple2<Long, String>> out) throws Exception {
        long key = value.getSiteId();
        OrderAccumulator cachedValue = state.get(key);

        if (cachedValue == null || value.getSubOrderSum() != cachedValue.getSubOrderSum()) {
            JSONObject result = new JSONObject();
            result.put("site_id", value.getSiteId());
            result.put("site_name", value.getSiteName());
            result.put("quantity", value.getQuantitySum());
            result.put("orderCount", value.getOrderIds().size());
            result.put("subOrderCount", value.getSubOrderSum());
            result.put("gmv", value.getGmv());
            out.collect(new Tuple2<>(key, result.toJSONString()));
            state.put(key, value);
        }
    }

    @Override
    public void close() throws Exception {
        state.clear();
        super.close();
    }
}