import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}

/**
 * val dataSet = benv.readTextFile("/path/to/data")
 * dataSet.writeAsText("/path/to/output")
 * benv.execute("My batch program")
 */
object BatchTest {
  def main(args: Array[String]): Unit = {
    var benv: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment


    val input = "F:/BaiduNetdiskDownload/26period/清华班-day08【flink下】/上午/data/orders.csv"
    var ds: DataSet[String] = benv.readTextFile(input)
    // ds.print()

    import org.apache.flink.api.scala.createTypeInformation
    // 第四个元素
    var res = ds.flatMap(_.split(",")(4))
      .map((_, 1))
      .groupBy(0)
      .sum(1)

    res.print()

  }

}
