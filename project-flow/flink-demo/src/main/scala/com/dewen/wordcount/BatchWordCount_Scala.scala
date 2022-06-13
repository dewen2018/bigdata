package com.dewen.wordcount

import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}

object BatchWordCount_Scala {

  def main(args: Array[String]): Unit = {
    //从外部命令中获取参数
    //    val tool: ParameterTool = ParameterTool.fromArgs(args)
    //    val input: String = tool.get("input")
    //    val output: String = tool.get("output")
    val input = "D:/codes/code2/bigdata/project-flow/flink-demo/data/word.txt"
    val output = "D:/codes/code2/bigdata/project-flow/flink-demo/data/result.csv"
    //构造执行环境
    // 1、env
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    //读取文件source
    val ds: DataSet[String] = env.readTextFile(input)
    // 其中flatMap 和Map 中  需要引入隐式转换

    import org.apache.flink.api.scala.createTypeInformation
    //transform:经过groupby进行分组，sum进行聚合
    // sink:flink所有的算子都可以设置并行度
    // val aggDs: AggregateDataSet[(String, Int)] =
    ds.flatMap(_.split(" "))
      .filter(_.nonEmpty)
      .map((_, 1))
      .groupBy(0)
      .sum(1)
      .writeAsCsv(output)
      .setParallelism(1)

    // 打印
    // aggDs.print()

    env.execute("BatchWordCount_Scala")
  }
}
