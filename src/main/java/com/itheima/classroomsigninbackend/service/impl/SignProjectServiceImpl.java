package com.itheima.classroomsigninbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.classroomsigninbackend.common.ResultCodeEnum;
import com.itheima.classroomsigninbackend.entity.Course;
import com.itheima.classroomsigninbackend.entity.SignProject;
import com.itheima.classroomsigninbackend.entity.SysUser;
import com.itheima.classroomsigninbackend.entity.UserCourse;
import com.itheima.classroomsigninbackend.exception.BusinessException;
import com.itheima.classroomsigninbackend.mapper.CourseMapper;
import com.itheima.classroomsigninbackend.mapper.SignProjectMapper;
import com.itheima.classroomsigninbackend.mapper.SysUserMapper;
import com.itheima.classroomsigninbackend.mapper.UserCourseMapper;
import com.itheima.classroomsigninbackend.service.SignProjectService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SignProjectServiceImpl extends ServiceImpl<SignProjectMapper, SignProject> implements SignProjectService {
    private static final int ROLE_TEACHER = 1;
    private static final int RECENT_DAYS = 7;

    private final CourseMapper courseMapper;
    private final UserCourseMapper userCourseMapper;
    private final SysUserMapper sysUserMapper;

    public SignProjectServiceImpl(CourseMapper courseMapper,
                                  UserCourseMapper userCourseMapper,
                                  SysUserMapper sysUserMapper) {
        this.courseMapper = courseMapper;
        this.userCourseMapper = userCourseMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public void createProject(String teacherId, String courseId, String projectName, LocalDateTime startTime,
                              LocalDateTime endTime, BigDecimal centerLat, BigDecimal centerLng, Integer radius) {
        if (teacherId == null || teacherId.isBlank()) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }
        SignProject project = new SignProject();
        project.setCourseId(courseId);
        project.setProjectName(projectName);
        project.setStartTime(startTime);
        project.setEndTime(endTime);
        project.setCenterLat(centerLat);
        project.setCenterLng(centerLng);
        project.setRadius(radius);
        save(project);
    }

    @Override
    public List<SignProject> listRecentProjects(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }
        SysUser user = sysUserMapper.selectById(userId);
        List<String> courseIds = user != null && user.getRole() != null && user.getRole() == ROLE_TEACHER
            ? listTeacherCourseIds(userId)
            : listStudentCourseIds(userId);
        if (courseIds.isEmpty()) {
            return Collections.emptyList();
        }
        LocalDateTime recentStart = LocalDateTime.now().minusDays(RECENT_DAYS);
        return list(new LambdaQueryWrapper<SignProject>()
            .in(SignProject::getCourseId, courseIds)
            .ge(SignProject::getEndTime, recentStart)
            .orderByDesc(SignProject::getStartTime));
    }

    @Override
    public List<SignProject> listProjects(String courseId, String status) {
        LambdaQueryWrapper<SignProject> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null && !courseId.isBlank()) {
            wrapper.eq(SignProject::getCourseId, courseId);
        }
        LocalDateTime now = LocalDateTime.now();
        if ("ongoing".equalsIgnoreCase(status)) {
            wrapper.le(SignProject::getStartTime, now).ge(SignProject::getEndTime, now);
        } else if ("history".equalsIgnoreCase(status)) {
            wrapper.lt(SignProject::getEndTime, now);
        }
        wrapper.orderByDesc(SignProject::getStartTime);
        return list(wrapper);
    }

    private List<String> listTeacherCourseIds(String teacherId) {
        List<Course> courses = courseMapper.selectList(new LambdaQueryWrapper<Course>()
            .eq(Course::getTeacherId, teacherId));
        return courses.stream().map(Course::getId).collect(Collectors.toList());
    }

    private List<String> listStudentCourseIds(String studentId) {
        List<UserCourse> userCourses = userCourseMapper.selectList(new LambdaQueryWrapper<UserCourse>()
            .eq(UserCourse::getStudentId, studentId));
        return userCourses.stream().map(UserCourse::getCourseId).collect(Collectors.toList());
    }
}
