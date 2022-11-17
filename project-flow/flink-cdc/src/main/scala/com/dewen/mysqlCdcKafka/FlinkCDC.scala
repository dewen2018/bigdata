package com.dewen.mysqlCdcKafka

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.source.SourceFunction

object FlinkCDC {

  /**
   * 写入kafka
   *
   * @param args
   */
  def main(args: Array[String]): Unit = {

    //1. 获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    //1.1 开启ck并指定状态后端fs
    //    env.setStateBackend(new FsStateBackend("hdfs://hadoop200:8020/gmall-flink-210325/ck"))
    //      .enableCheckpointing(10000L) //头尾间隔：每10秒触发一次ck
    //      env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)  //
    //      env.getCheckpointConfig.setCheckpointTimeout(10000L)
    //    env.getCheckpointConfig.setMaxConcurrentCheckpoints(2)
    //    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(3000l)  //尾和头间隔时间3秒

    //    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 10000L));

    //2. 通过flinkCDC构建SourceFunction并读取数据
    val sourceFunction: SourceFunction[String] = MySQLSource.builder[String]()
      .hostname("192.168.111.143")
      .port(3306)
      .databaseList("dewen0715")
      // set captured database
      //如果不添加该参数，则消费指定数据库中所有表的数据
      //如果添加，则需要按照 数据库名.表名 的格式指定，多个表使用逗号隔开
      .tableList("dewen0715.test_cdc") // set captured table
      .username("root")
      .password("Admin@123*")
      // .deserializer(new StringDebeziumDeserializationSchema())
      .deserializer(new CustomerDeseriallization())
      //监控的方式：
      .startupOptions(StartupOptions.latest())
      .build();

    val dataStream = env.addSource(sourceFunction)

    //3. sink, 写入kafka
    val sinkTopic = "ods_base_db"
    dataStream.addSink(MyKafkaUtil.getKafkaProducer(sinkTopic))

    dataStream.setParallelism(1).print()
    //4. 启动任务
    env.execute("flinkCDC")
  }
}