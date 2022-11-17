package com.dewen.mysqlCdcKafka


import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer

object MyKafkaUtil {

  val broker_list = "hadoop200:9092,hadoop201:9092,hadoop202:9092"

  def getKafkaProducer(topic: String): FlinkKafkaProducer[String] =
    new FlinkKafkaProducer[String](broker_list, topic, new SimpleStringSchema())
}