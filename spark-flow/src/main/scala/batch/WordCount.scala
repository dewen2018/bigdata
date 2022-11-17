package batch

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object WordCount {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder()
      .appName("word test")
      .master("local[2]")
      .enableHiveSupport()
      .getOrCreate()

    val file: String = "D:\\data\\bigdata\\The_Man_of_Property.txt"
    // 加载文件
    //    val lineRDD: RDD[String] = spark.sparkContext.textFile(file)
    //    val wordRDD: RDD[String] = lineRDD.flatMap(line => line.split(" "))
    //    val kvRDD: RDD[(String, Int)] = wordRDD.map(word => (word, 1))
    //    val wordCounts: RDD[(String, Int)] = kvRDD.reduceByKey((x, y) => x + y)
    //    wordCounts.foreach(wordCount => println(wordCount._1 + " appeared " + wordCount._2 + " times"))


    spark.sparkContext.textFile(file)
      .flatMap(line => line.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)
      .take(10)
      .foreach(k => println(k._1 + " " + k._2))
  }
}
