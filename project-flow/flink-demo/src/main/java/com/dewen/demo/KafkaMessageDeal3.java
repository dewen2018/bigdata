// package com.dewen.demo;
//
// import com.alibaba.fastjson.JSONObject;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.flink.api.common.functions.AggregateFunction;
// import org.apache.flink.api.common.serialization.SimpleStringSchema;
// import org.apache.flink.api.java.utils.ParameterTool;
// import org.apache.flink.streaming.api.CheckpointingMode;
// import org.apache.flink.streaming.api.TimeCharacteristic;
// import org.apache.flink.streaming.api.datastream.DataStream;
// import org.apache.flink.streaming.api.datastream.DataStreamSource;
// import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
// import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
// import org.apache.flink.streaming.api.functions.source.SourceFunction;
// import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
// import org.apache.flink.streaming.api.windowing.time.Time;
// import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
// import org.apache.flink.util.Collector;
//
// import java.util.Properties;
//
// @Slf4j
// public class KafkaMessageDeal3 {
//     public static void main(String[] args) throws Exception {
//         long delay = 5000L;
//         long windowGap = 5000L;
//
//         //1、设置运行环境
//         StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//         env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
//         env.enableCheckpointing(6000L);
//         env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
//         env.setParallelism(1);
//
//         //2、配置数据源读取数据
//         Properties props = new Properties();
//         props.put("bootstrap.servers", "172.21.88.77:9092");
//         props.put("group.id", KafkaProducer.TOPIC_GROUP1);
//
//         FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<String>(KafkaProducer.TOPIC_TEST, new SimpleStringSchema(), props);
//
//         // 配置规则服务
//         DataStream<Product> rulesStream = env.addSource(new MyRuleSource());
//
//         //设置数据源
//         DataStreamSource<String> dataStreamSource = env.addSource(consumer).setParallelism(1);
//
//         //从kafka里读取数据，转换成UserInfoVo对象
//         DataStream<UserInfoVo> dataStream = dataStreamSource.map(value -> JSONObject.parseObject(value, UserInfoVo.class));
//
//         dataStream
//                 .connect(rulesStream)
//                 .flatMap(new CoFlatMapFunction<UserInfoVo, Product, UserInfoVo>() {
//                     private Product localProduct;
//
//                     @Override
//                     public void flatMap1(UserInfoVo userInfoVo, Collector<UserInfoVo> collector) throws Exception {
//                         System.out.println("---------userInfoVo = " + userInfoVo);
//                         String[] id = userInfoVo.getId().split("_");
//                         String idTime = id[id.length - 1];
//                         int time = Integer.parseInt(idTime);
//                         if (localProduct.getDiscount() == 100) {
//                             if (time % 2 == 0) {
//                                 collector.collect(userInfoVo);
//                             }
//                         } else {
//                             if (time > 5) {
//                                 collector.collect(userInfoVo);
//                             }
//                         }
//                     }
//
//                     @Override
//                     public void flatMap2(Product product, Collector<UserInfoVo> collector) throws Exception {
//                         localProduct = product;
//                         System.out.println("---------product = " + product);
//                     }
//                 }).addSink(new MySqlSink2()).name("save mysql whit rule");
//
//         dataStream
//                 .keyBy("id")
//                 .window(SlidingProcessingTimeWindows.of(Time.seconds(60), Time.seconds(60)))
//                 .aggregate(new AggregateFunction<UserInfoVo, UserInfoCount, UserInfoCount>() {
//
//                     @Override
//                     public UserInfoCount createAccumulator() {
//                         UserInfoCount count = new UserInfoCount();
//                         count.setId(1);
//                         count.setUserId(null);
//                         count.setCount(0);
//                         return count;
//                     }
//
//                     @Override
//                     public UserInfoCount add(UserInfoVo userInfoVo, UserInfoCount userInfoCount) {
//                         if (userInfoCount.getUserId() == null) {
//                             userInfoCount.setUserId(userInfoVo.getId());
//                         }
//
//                         if (userInfoCount.getUserId().equals(userInfoVo.getId())) {
//                             userInfoCount.setCount(userInfoCount.getCount() + 1);
//                         }
//                         return userInfoCount;
//                     }
//
//                     @Override
//                     public UserInfoCount getResult(UserInfoCount userInfoCount) {
//                         return userInfoCount;
//                     }
//
//                     @Override
//                     public UserInfoCount merge(UserInfoCount userInfoCount, UserInfoCount acc1) {
//                         return null;
//                     }
//                 })
//                 .keyBy("userId")
//                 .addSink(new MySqlSink3());
//
//         env.execute("KafkaMessageDeal2");
//     }
//
//     private static DataStream<MyRule> getRulesUpdateStream(StreamExecutionEnvironment env, ParameterTool parameter) {
//         String name = "Rule Source";
//         SourceFunction<String> rulesSource = RulesSource.createRulesSource(parameter);
//         DataStream<String> rulesStrings = env.addSource(rulesSource).name(name).setParallelism(1);
//         return RulesSource.stringsStreamToRules(rulesStrings);
//     }
// }
