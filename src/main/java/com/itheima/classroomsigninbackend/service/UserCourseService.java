package com.itheima.classroomsigninbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.classroomsigninbackend.entity.UserCourse;

public interface UserCourseService extends IService<UserCourse> {
    void joinCourse(String studentId, String courseId);

    void leaveCourse(String studentId, String courseId);

    java.util.List<com.itheima.classroomsigninbackend.entity.SysUser> listStudents(String courseId);
}
