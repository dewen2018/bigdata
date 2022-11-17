package com.dewen

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala._

/**
 * flink sql从kafka读取嵌套json数据，并分别存入kafka与mysql数据库
 */

object realtime_data_to_kafka_mysql {

  def main(args: Array[String]): Unit = {

    //创建flink流处理环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val settings = EnvironmentSettings
      .newInstance()
      .useBlinkPlanner()
      .inStreamingMode()
      .build()

    val tableEnv = StreamTableEnvironment.create(env, settings)

    //创建作业名称
    tableEnv.getConfig().getConfiguration().setString("pipeline.name", "sink_to_kafka_mysql")

    //创建kafka数据源
    //1.result_list为数组结构，包含2个row，每个row包含2个字段
    tableEnv.executeSql("" +
      "create table input_table (" +
      "device string," +
      "id string," +
      "`type` string," +
      "result_list array<row<x1 string,y1 string>>)" +
      "with (" +
      "'connector'='kafka'," +
      "'topic'='topic_name'," +
      "'properties.bootstrap.servers'='192.168.xx.xx1:9092,192.168.xx.xx2:9092'," +
      "'properties.group.id'='testGroup'," +
      "'scan.startup.mode'='latest-offset'," +
      "'format'='json'" +
      ")"
    )

    //1.result_list为数组结构，且sql访问数组的下标从1开始，这里取出第一个row
    val inputTable = tableEnv.sqlQuery(
      "select device,id,`type`,result_list[1].x1 as x1, result_list[1].y1 as y1 from input_table")

    //注册临时表
    tableEnv.createTemporaryView("input_table", inputTable)

    //原始数据存入mysql数据库中
    tableEnv.executeSql(
      "create table realtime_raw_tmp(" +
        "device string," +
        "id string," +
        "`type` string," +
        "x1 string," +
        "y1 string" +
        ") with (" +
        "'connector'='jdbc'," +
        "'driver'='com.mysql.jdbc.Driver'," +
        "'url'='jdbc:mysql://192.168.xx.xxx:3306/数据库名'," +
        // 提前在mysql数据库中建好realtime_raw_table表
        "'table-name'='realtime_raw_table'," +
        "'username'='root'," +
        "'password'='Admin@123*')"
    )
    // 插入数据库
    tableEnv.executeSql("insert into realtime_raw_tmp select * from input_table")

    //写入kafka
    //创建kafka Sink
    tableEnv.executeSql(
      "create table output_table(" +
        "device string," +
        "id string," +
        "`type` string," +
        "x1 string," +
        "y1 string)" +
        "with (" +
        "'connector'='kafka'," +
        //在kafka中创建新topic_1
        "'topic'='topic_1'," +
        "'properties.bootstrap.servers'='192.168.xx.xx1:9092,192.168.xx.xx2:9092'," +
        "'format'='json'," +
        "'sink.partitioner'='round-robin'" +
        ")"
    )

    //向kafka sink写入数据
    tableEnv.executeSql("insert into output_table select * from " + inputTable)

    // env.execute()
  }

}