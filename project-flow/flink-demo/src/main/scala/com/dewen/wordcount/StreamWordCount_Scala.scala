package com.dewen.wordcount


import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

/**
 * ./flink run -c com.dewen.wordcount.StreamWordCount_Scala  /data/bigdata/flink-demo-1.0-SNAPSHOT.jar
 * bin/flink run --class com.dewen.wordcount.StreamWordCount /data/bigdata/flink-demo-1.0-SNAPSHOT.jar
 */
object StreamWordCount_Scala {
  def main(args: Array[String]): Unit = {

    //        val port: Int = try {
    //          ParameterTool.fromArgs(args).getInt("port")
    //        } catch {
    //          case e => {
    //            System.err.println("No port set, use default port 9999")
    //          }
    //            8888
    //        }

    //获取上下文对象
    val environment = StreamExecutionEnvironment.getExecutionEnvironment

    //获取数据
    val data = environment.socketTextStream("master", 9999)

    import org.apache.flink.api.scala._

    val window = data.flatMap(line => line.split("\\s"))
      .map(w => WordWithCount(w, 1))
      .keyBy("word")
      .timeWindow(Time.seconds(10), Time.seconds(2))

    window.sum("count").print().setParallelism(10)

    environment.execute("WordCount")
  }

  case class WordWithCount(word: String, count: Int)

}
