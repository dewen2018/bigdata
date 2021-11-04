package com.dewen.hellow

object kMeansjulei {
  def main(args: Array[String]): Unit = {
    val ks: Array[Int] = Array(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
    ks.foreach(cluster => {
      val model: KMeansModel = KMeans.train(parsedTrainingData, cluster, 30, 1)
      val ssd = model.computeCost(parsedTrainingData)
      println("sum of squared distances of points to their nearest center when k=" + cluster + " -> " + ssd)
    })
//    val conf = new SparkConf().setAppName("app").setMaster("local[*]")
    //    val sc = new SparkContext(conf)
    //    val textRDD = sc.textFile("F:\\log\\input\\mllib.csv")
    //    val paresdData = textRDD.filter(!_.contains("Region"))
    //      .map(line => {
    //        Vectors.dense(line.split(",").map(_.toDouble))
    //      }).cache()
    //    // •k 表示期望的聚类的个数，分成几类
    //    val numClusters = 2
    //    // •maxInterations 表示方法单次运行最大的迭代次数。
    //    val numIterations = 100
    //    val model = new KMeans()
    //      // 这里设置了分类的个数，也就是k值
    //      .setK(numClusters)
    //      .setMaxIterations(numIterations)
    //      .run(paresdData)
    //    // centers是k个中心坐标
    //    val centers = model.clusterCenters
    //    val k = model.k
    //    centers.foreach(println)
    //    // 保存训练过后的模型
    //    model.save(sc, "f:/log/out/Kmeans/Kmeans_model4")
    //    // 加载模型
    //    val sameModel = KMeansModel.load(sc, "f:/log/out/Kmeans/Kmeans_model4")
    //    // 验证模型是否正确
    //    print(sameModel.predict(Vectors.dense(9, 9)))
    //    //SparkSQL读取显示2个中心点坐标
    //    val sqlContext = new SQLContext(sc)
    //    sqlContext.read.parquet("f:/log/out/Kmeans/Kmeans_model4/data").show()
  }

}
