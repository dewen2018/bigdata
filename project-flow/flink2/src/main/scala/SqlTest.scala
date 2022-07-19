import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment, createTypeInformation}
import org.apache.flink.table.api.{Table, TableEnvironment}

object SqlTest {

  case class Ailse(ailse_id: String, ailse: String)

  def main(args: Array[String]): Unit = {
    var benv: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    var ds: DataSet[Ailse] = benv.readCsvFile[Ailse]("F:/BaiduNetdiskDownload/26period/清华班-day08【flink下】/上午/data/aisles.csv")
    // ds.print()
    // table env
    var tenv = TableEnvironment.getTableEnvironment(benv)
    tenv.registerDataSet("aisles", ds)
    // query result
    var res: Table = tenv.sqlQuery("select * from aisles")
    res.printSchema()
    // res.toDataSet[Ailse].print()

    // res.groupBy("ailse")

  }

}
