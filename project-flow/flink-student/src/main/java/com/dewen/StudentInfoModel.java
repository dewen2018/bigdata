package com.dewen;

import lombok.Data;

import java.io.Serializable;

@Data
public class StudentInfoModel implements Serializable {
    private String time = "0";
    private String name;
    private String eventId;
    private String ip;
    private Long learnLength = 0L; // 学习实操
    private Long speakAmount = 0L; // 发言次数
    private Long answerAmount = 0L; // 答题次数
}