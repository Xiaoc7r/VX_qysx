package com.itheima.classroomsigninbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.classroomsigninbackend.common.ResultCodeEnum;
import com.itheima.classroomsigninbackend.entity.SysUser;
import com.itheima.classroomsigninbackend.entity.UserCourse;
import com.itheima.classroomsigninbackend.exception.BusinessException;
import com.itheima.classroomsigninbackend.mapper.SysUserMapper;
import com.itheima.classroomsigninbackend.mapper.UserCourseMapper;
import com.itheima.classroomsigninbackend.service.UserCourseService;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UserCourseServiceImpl extends ServiceImpl<UserCourseMapper, UserCourse> implements UserCourseService {
    private final SysUserMapper sysUserMapper;

    public UserCourseServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public void joinCourse(String studentId, String courseId) {
        long count = count(new LambdaQueryWrapper<UserCourse>()
            .eq(UserCourse::getStudentId, studentId)
            .eq(UserCourse::getCourseId, courseId));
        if (count > 0) {
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR.getCode(), "Already joined");
        }
        UserCourse userCourse = new UserCourse();
        userCourse.setStudentId(studentId);
        userCourse.setCourseId(courseId);
        save(userCourse);
    }

    @Override
    public void leaveCourse(String studentId, String courseId) {
        remove(new LambdaQueryWrapper<UserCourse>()
            .eq(UserCourse::getStudentId, studentId)
            .eq(UserCourse::getCourseId, courseId));
    }

    @Override
    public List<SysUser> listStudents(String courseId) {
        List<UserCourse> userCourses = list(new LambdaQueryWrapper<UserCourse>()
            .eq(UserCourse::getCourseId, courseId));
        if (userCourses.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> studentIds = userCourses.stream()
            .map(UserCourse::getStudentId)
            .collect(Collectors.toList());
        return sysUserMapper.selectBatchIds(studentIds);
    }
}
