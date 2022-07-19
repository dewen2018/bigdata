package com.dewen.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 这是一张测试表
 *
 * @author dsj
 * @since 2020-10-15
 */
@Data
@TableName("dsj")
public class Dsj {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "dsj_id")
    private String dsjId;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dsjDate;

    /**
     * 日期时间
     */
    // @JsonFormat(pattern = "yyyyMMdd'T'HHmmss'Z'")
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dsjDatetime;


}