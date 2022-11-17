package com.dewen;

import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 汇总计算逻辑
 * 计算每个学生和整个课堂的数据
 */
public class StudentLearnRichProcessFunction extends ProcessAllWindowFunction<StudentInfoModel, String, TimeWindow> {
    private static final String CLASS_IN = "class_in";
    private static final String SPEAK = "speak";
    private static final String ANSWER = "answer";
    private static final Long WISH_STUDENT_AMOUNT = 10L;
    private MapState<String, StudentInfoModel> studentLearnMapState;
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void open(Configuration parameters) throws Exception {
        // 注册状态
        // MapState :key是一个唯一的值，value是接收到的相同的key对应的value的值
        MapStateDescriptor<String, StudentInfoModel> descriptor = new MapStateDescriptor<>(
                // 状态的名字
                "studentLearn", String.class, StudentInfoModel.class);
        // 状态存储的数据类型
        studentLearnMapState = getRuntimeContext().getMapState(descriptor);
    }

    @Override
    public void process(ProcessAllWindowFunction<StudentInfoModel, String, TimeWindow>.Context context, Iterable<StudentInfoModel> elements, Collector<String> collector) throws Exception {
        // 遍历本次窗口中的所有数据
        for (StudentInfoModel studentInfoModel : elements) {
            // 先把本次窗口内新增数据更新到状态中
            updateStudentInfo(studentInfoModel);
        }
        Date date = new Date();
        String curTime = df.format(date);
        // 计算整个课程的数据和学生的数据
        long realStudentAmount = 0;
        long speakStudentAmount = 0;
        long answerStudentAmount = 0;
        for (String name : studentLearnMapState.keys()) {
            StudentInfoModel studentInfoState = studentLearnMapState.get(name);
            // 计算单个学生的数据并推送
            statStudentLearnData(name, studentInfoState, curTime, collector);
            // 计算整个课程的数据
            //    System.out.println("index: "+index+" name:"+name+" studentInfoStIterable<StudentInfoModel>elements,Collector<String> collectate:" + studentInfoState.toString());
            realStudentAmount += 1;
            if (studentInfoState.getSpeakAmount() > 0) {
                // 表示该学生有过发言
                speakStudentAmount += 1;
            }
            if (studentInfoState.getAnswerAmount() > 0) { // 学生有过答题
                answerStudentAmount += 1;
            }
        }
        // 汇总输出
        if (realStudentAmount > 0) {
            double LearnStudentRate = 100 * realStudentAmount / WISH_STUDENT_AMOUNT;
            double speakStudentRate = 100 * speakStudentAmount / realStudentAmount;
            double answerStudentRate = 100 * answerStudentAmount / realStudentAmount;
            String courseDataSummary = "【课堂数据】时间:" + curTime
                    + " 应到课人数:" + WISH_STUDENT_AMOUNT + " 教室的人数:" + realStudentAmount
                    + " 到课率:" + LearnStudentRate + "%" + " 互动发言人数:" + speakStudentAmount + " 发言率:" + speakStudentRate + "%" + " 答题人数:" + answerStudentAmount
                    + " 答题率:" + answerStudentRate + "%";
            collector.collect(courseDataSummary);
        }
    }

    private void statStudentLearnData(String name, StudentInfoModel studentInfoState, String curTime, Collector<String> collector) throws Exception {
        StudentInfoModel stundentInfoState = studentLearnMapState.get(name);
        String studentLearnDataSummary = "【学生数据】时间:" + curTime + " 姓名:" + name
                + " 到课时长:" + stundentInfoState.getLearnLength() / 1000 + " 发言次数:" + stundentInfoState.getSpeakAmount()
                + " 答题次数:" + stundentInfoState.getAnswerAmount();
        // 把学生的数据写到下游
        collector.collect(studentLearnDataSummary);
    }

    private void updateStudentInfo(StudentInfoModel studentInfoModel) throws Exception {
        String name = studentInfoModel.getName();
        StudentInfoModel studentInfoState = studentLearnMapState.get(name);
        System.out.println("updateStudentInfo:" + studentInfoModel.toString());
        String eventId = studentInfoModel.getEventId();
        // 当前这个学生没有历史状态数据，直接更新进去
        if (studentInfoState == null) {
            studentInfoState = new StudentInfoModel();
            if (CLASS_IN.equals(eventId)) {
                long learnLength = new Date().getTime() - Long.parseLong(studentInfoModel.getTime());
                studentInfoState.setLearnLength(learnLength);
            } else if (SPEAK.equals(eventId)) {
                studentInfoState.setSpeakAmount(1L);
            } else if (ANSWER.equals(eventId)) {
                studentInfoState.setAnswerAmount(1L);
            } else {
                if (CLASS_IN.equals(eventId)) {
                    long learnLength = new Date().getTime() - Long.parseLong(studentInfoModel.getTime());
                    studentInfoState.setLearnLength(learnLength + studentInfoState.getLearnLength());
                } else if (SPEAK.equals(eventId)) {
                    studentInfoState.setSpeakAmount(1L + studentInfoState.getSpeakAmount());
                } else if (ANSWER.equals(eventId)) {
                    studentInfoState.setAnswerAmount(1L + studentInfoState.getAnswerAmount());
                }
            }
            // 更新状态
            studentLearnMapState.put(name, studentInfoState);
        }
    }
}

