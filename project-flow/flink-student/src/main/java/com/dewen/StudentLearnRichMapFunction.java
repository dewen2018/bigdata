package com.dewen;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RichMapFunction;

import java.util.Date;
import java.util.Objects;

/**
 * 将source端的字符串日志转换成对象，便于下游使用
 */
public class StudentLearnRichMapFunction extends RichMapFunction<String, StudentInfoModel> {
    @Override
    public StudentInfoModel map(String input) throws Exception {
        if (Objects.isNull(input)) {
            return null;
        }
        JSONObject inputJson = JSONObject.parseObject(input);
        // 本次测试阶段，我们使用当前时间戳来替代事件时间
        StudentInfoModel studentInfoModel = new StudentInfoModel();
        Date date = new Date();
        studentInfoModel.setTime(String.valueOf(date.getTime()));
        studentInfoModel.setName(inputJson.getString("name"));
        studentInfoModel.setEventId(inputJson.getString("event_id"));
        studentInfoModel.setIp(inputJson.getString("ip"));
        System.out.println("studentInfoModel:" + studentInfoModel.toString());
        return studentInfoModel;
    }
}