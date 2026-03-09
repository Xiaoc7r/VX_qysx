package com.itheima.classroomsigninbackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.classroomsigninbackend.common.BaseEntity;

@TableName("user_course")
public class UserCourse extends BaseEntity {
    @TableField("student_id")
    private String studentId;

    @TableField("course_id")
    private String courseId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
