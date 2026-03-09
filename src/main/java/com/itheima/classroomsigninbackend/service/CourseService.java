package com.itheima.classroomsigninbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.classroomsigninbackend.entity.Course;
import java.util.List;

public interface CourseService extends IService<Course> {
    void createCourse(String teacherId, String courseName, String courseDesc);

    List<Course> listCourses(String type, String userId);
}
