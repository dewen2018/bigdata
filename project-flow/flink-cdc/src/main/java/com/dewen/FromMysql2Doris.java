package com.dewen;

import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.doris.flink.cfg.DorisExecutionOptions;
import org.apache.doris.flink.cfg.DorisOptions;
import org.apache.doris.flink.cfg.DorisSink;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Date;
import java.util.Properties;

public class FromMysql2Doris {

    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // enable checkpoint
        env.enableCheckpointing(3000);
        // source
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("192.168.111.143")
                .port(3306)
                .databaseList("dewen0715") // set captured database
                .tableList("dewen0715.test_cdc") // set captured table
                .username("root")
                .password("Admin@123*")
                .deserializer(new JsonDebeziumDeserializationSchema()).build();

        Properties pro = new Properties();
        pro.setProperty("format", "json");
        pro.setProperty("strip_outer_array", "true");


        /**
         * flink cdc同步数据时的数据几种数据格式：
         * insert  :{"before":null,"after":{"id":30,"name":"wangsan","age":27,"address":"北京"},"source":{"version":"1.5.4.Final","connector":"mysql","name":"mysql_binlog_source","ts_ms":1652844463000,"snapshot":"false","db":"test","sequence":null,"table":"test_cdc","server_id":1,"gtid":null,"file":"mysql-bin.000001","pos":10525,"row":0,"thread":null,"query":null},"op":"c","ts_ms":1652844463895,"transaction":null}
         * update :{"before":{"id":30,"name":"wangsan","age":27,"address":"北京"},"after":{"id":30,"name":"wangsan","age":27,"address":"上海"},"source":{"version":"1.5.4.Final","connector":"mysql","name":"mysql_binlog_source","ts_ms":1652844511000,"snapshot":"false","db":"test","sequence":null,"table":"test_cdc","server_id":1,"gtid":null,"file":"mysql-bin.000001","pos":10812,"row":0,"thread":null,"query":null},"op":"u","ts_ms":1652844511617,"transaction":null}
         * delete :{"before":{"id":25,"name":"wanger","age":26,"address":"西安"},"after":null,"source":{"version":"1.5.4.Final","connector":"mysql","name":"mysql_binlog_source","ts_ms":1652844336000,"snapshot":"false","db":"test","sequence":null,"table":"test_cdc","server_id":1,"gtid":null,"file":"mysql-bin.000001","pos":10239,"row":0,"thread":null,"query":null},"op":"d","ts_ms":1652844336733,"transaction":null}
         *
         * doris:
         * 因为doris是不能根据非主键字段做删除操作的，所以当mysql中有删除操作时，这边边采用逻辑删除的方式，将删出字段标记为已删除
         * 后期在做数据分析时，可以将这部分数据过滤掉即可。
         *  is_delete：逻辑删除标志符 0表示正常 1表示删除
         *  updateStamp：数据的落库时间
         */

        DataStreamSource<String> dataStreamSource = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "mysql source");
        // 逻辑处理1.map
        dataStreamSource.map(line -> {
            JSONObject lineObj = JSONObject.parseObject(line);
            //如果是insert或者update操作，则取after中的数据，将是否删除设置为0
            JSONObject data;
            if ("d".equals(lineObj.getString("op"))) {
                //如果是delete操作，则取before中的数据，将其设置为1
                data = JSONObject.parseObject(lineObj.getString("before"));
                data.put("is_delete", 1);
                data.put("updateStamp", new Date().toLocaleString());
            } else {
                data = JSONObject.parseObject(lineObj.getString("after"));
                data.put("is_delete", 0);
                data.put("updateStamp", new Date().toLocaleString());
            }
            return data.toJSONString();
        })
                // sink
                .addSink(
                        DorisSink.sink(
                                // executionOptions
                                DorisExecutionOptions.builder()
                                        .setBatchSize(3)
                                        .setBatchIntervalMs(10L)
                                        .setMaxRetries(3)
                                        .setStreamLoadProp(pro)
                                        .setEnableDelete(true)
                                        .build(),
                                // dorisOptions
                                DorisOptions.builder()
                                        .setFenodes("192.168.111.143:8030")
                                        .setTableIdentifier("dewen0718.doris_test")
                                        .setUsername("root")
                                        .setPassword("")
                                        .build()
                        ));

        dataStreamSource.setParallelism(1).print();
        env.execute("Print MySQL Snapshot + Binlog");
    }

}
