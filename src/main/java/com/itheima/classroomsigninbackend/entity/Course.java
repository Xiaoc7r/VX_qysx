package com.itheima.classroomsigninbackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.classroomsigninbackend.common.BaseEntity;

@TableName("course")
public class Course extends BaseEntity {
    @TableField("course_name")
    private String courseName;

    @TableField("course_desc")
    private String courseDesc;

    @TableField("teacher_id")
    private String teacherId;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDesc() {
        return courseDesc;
    }

    public void setCourseDesc(String courseDesc) {
        this.courseDesc = courseDesc;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
}
