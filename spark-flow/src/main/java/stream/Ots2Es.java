package stream;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;

/**
 * 从OTS
 * 导入测试数据到
 * ES
 */
public class Ots2Es {
    public static void main(String[] args) throws StreamingQueryException, AnalysisException {
        SparkSession spark = null;
        //Config configs = ConfigFactory.load(ConfigFactory.parseFile(new java.io.File(args[0])));
        // only config
        spark = SparkSession
                .builder()
                .appName("test").master("local")
                .getOrCreate();

        spark.sparkContext().setLogLevel("warn");

        spark.readStream()
                .format("org.apache.spark.sql.aliyun.tablestore.TableStoreSourceProvider")
                .option("table.name", "test_ots")
                .option("tunnel.id", "1111111111111111")
                .option("endpoint", "https://test.cn-beijing.ots.aliyuncs.com")
                .option("instance.name", "test-xxx")
                .option("access.key.id", "xxxxx")
                .option("access.key.secret", "xxxx")
                .option("maxoffsetsperchannel", 50000) // default 10000
                .option("catalog", "{\"columns\":{" +
                        "  \"id_md5\":{\"type\":\"string\"}," +
                        "  \"id\":{\"type\":\"string\"}," +
                        "  \"_version\":{\"type\":\"string\"}," +
                        "  \"authUser\":{\"type\":\"string\"}" +
                        "  }}")
                .load()
                .createTempView("tables_view");

//        spark.sql("select * from tables_view")
//                .writeStream()
//                .format("console")
//                .start();

        StreamingQuery esQuery = spark.sql("select id as id," +
                        "  _version as version," +
                        "  split(regexp_replace(authUser, '\\\\[|\\\\]|\\\"', ''),'\\,') as authUser," +
                        "  \"true\" as valid from tables_view")
                .writeStream()
                .outputMode("append")
                .format("org.elasticsearch.spark.sql")
                .option("es.nodes.wan.only", true)
                .option("es.net.http.auth.user", "xxxx")
                .option("es.net.http.auth.pass", "xxxx")
                .option("es.nodes", "xxxx.elasticsearch.aliyuncs.com")
                .option("es.port", "9200")
                .option("checkpointLocation", "./offset/xxxxx/es/")
                .option("triggerInterval", 10000)
                .option("es.mapping.id", "id")
                .start("xxxxxx_index");

        // try{
        esQuery.awaitTermination();
        //  }catch (Exception ex){
        //    System.out.println(ex.getStackTrace());
        // }
    }
}
// #! /bin/bash
// nohup spark-submit \
// --class com.xxx.stream.Ots2Es \
// --master yarn \
// --deploy-mode client \
// --name xxxx \
// --num-executors 1 \
// --executor-memory 1g \
// --executor-cores 1 \
// --files hdfs:///emr-config/xxx/xxx.properties \
// --jars oss://xxx.aliyuncs.com/test/stream-ots.jar xxx.properties &