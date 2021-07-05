package com.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class PostGraduate {
//    @ExcelProperty(index = 2)
    Integer id;
    Integer stu_id;
    String name;
    Integer major_code;
//    @ExcelProperty(value = "计算机科学与技术")
    String major_name;
    Integer preExamSumScore;
    Integer postWrittenScore;
    Integer postComputerScore;
    Double postInterviewScore;
    Double postSumScore;
    Double postExamPercent;
    Double totalScore;
    Boolean isAdjustment;
    String remark;
    Integer softwareEngineScore;
    Integer databaseScore;
}
