import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, WindowedStream}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

/**
 * 流处理
 */
object SteamTest {

  def main(args: Array[String]): Unit = {
    var senv: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    // 求和
    // val ds: DataStream[Int] = senv.fromElements(1, 2, 3, 4)
    // ds.countWindowAll(2).sum(0).print()

    var ds: DataStream[String] = senv.socketTextStream("master", 9999)

    var window: WindowedStream[(String, Int), Tuple, TimeWindow] = ds.flatMap(_.split(" "))
      .map((_, 1))
      .keyBy(0)
      // 窗口长度10，步长2
      .timeWindow(Time.seconds(10), Time.seconds(2))

    // 窗口内的和，并行度10
    window.sum(1)
      .print()
      .setParallelism(10)

    senv.execute("My streaming program")
  }
}
