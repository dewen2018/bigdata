package com.dewen;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class CdcFromMysql {
    public static void main(String[] args) throws Exception {
        try {
            // 初始化flink流处理的运行环境
            final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

            env.enableCheckpointing(3000);
            // 使用MySQLSource创建数据源
            // 同时指定StringDebeziumDeserializationSchema，将CDC转换为String类型输出
            MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                    .hostname("192.168.111.143")
                    .port(3306)
                    .databaseList("dewen0715") // set captured database
                    .tableList("dewen0715.test_cdc") // set captured table
                    .username("root")
                    .password("Admin@123*")
                    .deserializer(new JsonDebeziumDeserializationSchema()).build();

            // set the source parallelism to 1
            env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "mysql source")
                    .setParallelism(1)
                    .print();

            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}