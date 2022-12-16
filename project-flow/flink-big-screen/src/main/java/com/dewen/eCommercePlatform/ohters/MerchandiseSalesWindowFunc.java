package com.dewen.eCommercePlatform.ohters;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * @author dewen
 * @date 2022/12/6 17:12
 */
public class MerchandiseSalesWindowFunc implements WindowFunction<Long, Tuple2<Long, Long>, Tuple, TimeWindow> {
    private static final long serialVersionUID = 1L;

    @Override
    public void apply(Tuple key, TimeWindow window, Iterable<Long> accs, Collector<Tuple2<Long, Long>> out) throws Exception {
        long merchId = ((Tuple1<Long>) key).f0;
        long acc = accs.iterator().next();
        out.collect(new Tuple2<>(merchId, acc));
    }
}
