package com.itheima.classroomsigninbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.classroomsigninbackend.common.ResultCodeEnum;
import com.itheima.classroomsigninbackend.entity.Course;
import com.itheima.classroomsigninbackend.exception.BusinessException;
import com.itheima.classroomsigninbackend.mapper.CourseMapper;
import com.itheima.classroomsigninbackend.service.CourseService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    @Override
    public void createCourse(String teacherId, String courseName, String courseDesc) {
        if (teacherId == null || teacherId.isBlank()) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }
        Course course = new Course();
        course.setTeacherId(teacherId);
        course.setCourseName(courseName);
        course.setCourseDesc(courseDesc);
        save(course);
    }

    @Override
    public List<Course> listCourses(String type, String userId) {
        if ("all".equalsIgnoreCase(type)) {
            return list();
        }
        if ("my".equalsIgnoreCase(type)) {
            return list(new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, userId));
        }
        throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR.getCode(), "Invalid course list type");
    }
}
