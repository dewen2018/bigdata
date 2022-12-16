代码参考：https://mp.weixin.qq.com/s/4UChwGmOhkhv44RdS-GkgA
flink-cdc获取到数据，写入数据到kafka，flink-streaming消费kafka将计算结果保存到redis
0.MysqlCdc2Kafka，flink-cdc读取after数据sink到kafka。
1.Kafka2redis：根据siteId聚合
2.Kafka2redisTopN：商铺销量topN
数据源复用需要了解