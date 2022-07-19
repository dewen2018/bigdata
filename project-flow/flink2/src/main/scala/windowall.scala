import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

object windowall {

  // 在窗口有效期内做聚合

  def main(args: Array[String]): Unit = {

    var senv: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    var ds: DataStream[String] = senv.socketTextStream("master", 9999)

    var window: scala.AllWindowedStream[(String, Int), TimeWindow] = ds.flatMap(_.split(" "))
      .map((_, 1))
      .keyBy(0)
      // 窗口长度10，步长2
      // 滑动窗口
      // .timeWindowAll(Time.seconds(5), Time.seconds(1))
      // 翻转窗口
      .timeWindowAll(Time.seconds(5), Time.seconds(1))

    // 窗口内的和，并行度10
    window.sum(1)
      .print()
      .setParallelism(10)

    senv.execute("My streaming program")
  }
}
