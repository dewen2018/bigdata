package com.dewen;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * 读取文件，并间隔5s将数据写到下游
 */
public class StudentLearnDataSource implements SourceFunction<String> {
    @Override
    public void run(SourceContext<String> sourceContext) throws Exception {
        try {
            String inputFilePath = "D:\\codes\\code2\\bigdata\\project-flow\\flink-student\\data\\student_event_data.txt";
            File file = new File(inputFilePath);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String strLine = null;
            while (null != (strLine = bufferedReader.readLine())) {
                sourceContext.collect(strLine);
                // 执行一条sleep 5s
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancel() {
    }

    public static void main(String[] args) throws Exception {
        new StudentLearnDataSource().run(null);
    }
}