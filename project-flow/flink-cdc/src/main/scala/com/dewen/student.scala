package com.dewen

import java.util.{Properties, Random}

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer


case class student(name: String, age: Int)

object ReadSource {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    //    readFromCollection(env)   //1.从集合中获取数据fromCollection
    //
    //      readFromTextFile(env)     //从文件中获取数据源
    //
    //    readFromKafka(env)        //从kafka中获取数据
    //
    //    readFromSocket(env)        //从网络中获取数据

    readFromMySource(env) //从自定义数据源中获取数据

    env.execute("StreamSource")
  }

  /**
   * 从集合中获取数据源
   *
   * @param env
   */
  def readFromCollection(env: StreamExecutionEnvironment): Unit = {
    val dataStream1 = env.fromCollection(List("jelly", 1))
    val list = List(student("zhangsan", 15), student("lisi", 16))
    val dataStream2 = env.fromCollection(list)
    dataStream2.print()
    dataStream1.print()
  }

  /**
   * 从文件中获取数据源
   *
   * @param env
   */
  def readFromTextFile(env: StreamExecutionEnvironment): Unit = {
    val dataStream3: DataStream[String] = env.readTextFile("D:\\program\\FlinkOneTest\\src\\main\\resources\\hello.txt")
    dataStream3.print()
  }

  /**
   * 从kafaka中获取数据源
   *
   * @param env
   */
  def readFromKafka(env: StreamExecutionEnvironment): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val props = new Properties()
    props.setProperty("bootstrap.servers", "localhost:9092")
    props.setProperty("group.id", "flink-group")
    env.addSource(new FlinkKafkaConsumer[String]("test", new SimpleStringSchema(), props))
      .print()
  }

  /**
   * 从网络中获取数据源
   *
   * @param env
   */
  def readFromSocket(env: StreamExecutionEnvironment): Unit = {
    env.socketTextStream("localhost", 9999).print()
  }

  /**
   * 从自定义Source中获取数据
   *
   * @param env
   */
  def readFromMySource(env: StreamExecutionEnvironment): Unit = {
    env.addSource(new MySourceFunction).print()
  }
}

/**
 * 自定义一个MySourceFunction，他要继承SourceFunction，重写run和cancel方法
 */
class MySourceFunction extends SourceFunction[student] {
  var running = true

  override def run(sourceContext: SourceFunction.SourceContext[student]): Unit = {
    while (running) {
      val random = new Random()
      val stu = student("name_" + random.nextInt(10), random.nextInt(100))
      sourceContext.collect(stu)
    }
  }

  override def cancel(): Unit = running = false
}