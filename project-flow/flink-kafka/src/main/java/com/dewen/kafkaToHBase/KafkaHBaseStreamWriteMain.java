package com.dewen.kafkaToHBase;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class KafkaHBaseStreamWriteMain {
    public static String TOPIC = "dewen";

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.addSource(new FlinkKafkaConsumer<>(
                TOPIC,   //这个 kafka topic 需要和上面的工具类的 topic 一致
                new SimpleStringSchema(),
                getKafkaProps()))
                .writeUsingOutputFormat(new HBaseOutputFormat());

        env.execute("Flink HBase connector sink");
    }


    private static Properties getKafkaProps() {
        // 配置kafka
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.252.92.4:9092");
        props.put("zookeeper.connect", "10.252.92.4:2181");
        props.put("group.id", "metric-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    private static class HBaseOutputFormat implements OutputFormat<String> {
        private org.apache.hadoop.conf.Configuration configuration;
        private Connection connection = null;
        private Table table = null;

        @Override
        public void configure(Configuration parameters) {
            // 配置Hbase
            configuration = HBaseConfiguration.create();
            configuration.set("hbase.zookeeper.quorum", "10.252.92.4:2181");
            configuration.set("hbase.zookeeper.property.clientPort", "2081");
            configuration.set("hbase.rpc.timeout", "30000");
            configuration.set("hbase.client.operation.timeout", "30000");
            configuration.set("hbase.client.scanner.timeout.period", "30000");
        }

        @Override
        public void open(int taskNumber, int numTasks) throws IOException {
            connection = ConnectionFactory.createConnection(configuration);
            TableName tableName = TableName.valueOf("zhisheng_stream");
            Admin admin = connection.getAdmin();
            if (!admin.tableExists(tableName)) { //检查是否有该表，如果没有，创建
                log.info("不存在表:{}", tableName);
                admin.createTable(
                        new HTableDescriptor(TableName.valueOf("zhisheng_stream"))
                                .addFamily(new HColumnDescriptor("info_stream")));
            }
            table = connection.getTable(tableName);
        }

        @Override
        public void writeRecord(String record) throws IOException {
            log.info("rowkey->{},column->info_stream:{},value->{}", record.substring(6, 10), record, "cwf_" + record);
            Put put = new Put(Bytes.toBytes(record.substring(6, 10)));
            put.addColumn(Bytes.toBytes("info_stream"), Bytes.toBytes(record), Bytes.toBytes("cwf_" + record));
            table.put(put);
        }

        @Override
        public void close() throws IOException {
            table.close();
            connection.close();
        }
    }
}