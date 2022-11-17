package com.dewen.mysqlCdcKafka

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.source.SourceFunction

object FlinkCDCTest {
  // 读取MySQL binlog
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 使用MySQLSource创建数据源
    // 同时指定StringDebeziumDeserializationSchema，将CDC转换为String类型输出
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
      // 1. initial 初始化全表拷贝，然后再比较
      // 2. earliest 不做初始化，只从当前的
      // 3. latest  指定最新的
      // 4. specificOffset 指定offset
      // 3. timestamp 比指定的时间大的
      .startupOptions(StartupOptions.latest())
      .build();

    // 单并行度打印，避免输出乱序
    env.addSource(sourceFunction).print.setParallelism(1)

    env.execute()
  }
}
