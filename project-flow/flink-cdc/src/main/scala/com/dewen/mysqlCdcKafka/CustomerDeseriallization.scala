package com.dewen.mysqlCdcKafka

import com.alibaba.fastjson.JSONObject
import com.alibaba.ververica.cdc.debezium.DebeziumDeserializationSchema
import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeInformation}
import org.apache.flink.util.Collector
import org.apache.kafka.connect.data.{Schema, Struct}
import org.apache.kafka.connect.source.SourceRecord


class CustomerDeseriallization extends DebeziumDeserializationSchema[String] {

  /**
   * 封装的数据：
   * {
   * "database":"",
   * "tableName":"",
   * "type":"c r u d",
   * "before":"",
   * "after":"",
   * "ts": ""
   * }
   *
   * @param sourceRecord
   * @param collector
   */
  override def deserialize(sourceRecord: SourceRecord, collector: Collector[String]): Unit = {
    //1. 创建json对象用于保存最终数据
    val result = new JSONObject()


    val value: Struct = sourceRecord.value().asInstanceOf[Struct]
    //2. 获取库名&表名
    val source: Struct = value.getStruct("source")
    val database = source.getString("db")
    val table = source.getString("table")

    //3. 获取before
    val before = value.getStruct("before")
    val beforeObj = if (before != null) getJSONObjectBySchema(before.schema(), before) else new JSONObject()


    //4. 获取after
    val after = value.getStruct("after")
    val afterObj = if (after != null) getJSONObjectBySchema(after.schema(), after) else new JSONObject()

    //5. 获取操作类型
    val op: String = value.getString("op")

    //6. 获取操作时间
    val ts = source.getInt64("ts_ms")
    //    val ts = value.getInt64("ts_ms")


    //7. 拼接结果
    result.put("database", database)
    result.put("table", table)
    result.put("type", op)
    result.put("before", beforeObj)
    result.put("after", afterObj)
    result.put("ts", ts)

    collector.collect(result.toJSONString)

  }


  override def getProducedType: TypeInformation[String] = {
    BasicTypeInfo.STRING_TYPE_INFO
  }


  //从Schema中获取字段和值
  def getJSONObjectBySchema(schema: Schema, struct: Struct): JSONObject = {
    val fields = schema.fields()
    var jsonBean = new JSONObject()
    val iter = fields.iterator()
    while (iter.hasNext) {
      val field = iter.next()
      val key = field.name()
      val value = struct.get(field)
      jsonBean.put(key, value)
    }
    jsonBean
  }

}