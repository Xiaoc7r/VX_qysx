package com.itheima.classroomsigninbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.classroomsigninbackend.dto.StatDTO;
import com.itheima.classroomsigninbackend.entity.SignProject;
import com.itheima.classroomsigninbackend.entity.SignRecord;
import com.itheima.classroomsigninbackend.entity.UserCourse;
import com.itheima.classroomsigninbackend.mapper.SignProjectMapper;
import com.itheima.classroomsigninbackend.mapper.SignRecordMapper;
import com.itheima.classroomsigninbackend.mapper.UserCourseMapper;
import com.itheima.classroomsigninbackend.service.StatService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StatServiceImpl implements StatService {
    private static final int STATUS_SIGNED = 1;

    private final UserCourseMapper userCourseMapper;
    private final SignProjectMapper signProjectMapper;
    private final SignRecordMapper signRecordMapper;

    public StatServiceImpl(UserCourseMapper userCourseMapper,
                           SignProjectMapper signProjectMapper,
                           SignRecordMapper signRecordMapper) {
        this.userCourseMapper = userCourseMapper;
        this.signProjectMapper = signProjectMapper;
        this.signRecordMapper = signRecordMapper;
    }

    @Override
    public StatDTO getPersonalStat(String studentId) {
        List<UserCourse> userCourses = userCourseMapper.selectList(new LambdaQueryWrapper<UserCourse>()
            .eq(UserCourse::getStudentId, studentId));
        List<String> courseIds = userCourses.isEmpty()
            ? Collections.emptyList()
            : userCourses.stream().map(UserCourse::getCourseId).collect(Collectors.toList());
        long totalCount = courseIds.isEmpty()
            ? 0
            : signProjectMapper.selectCount(new LambdaQueryWrapper<SignProject>()
                .in(SignProject::getCourseId, courseIds));
        long signedCount = signRecordMapper.selectCount(new LambdaQueryWrapper<SignRecord>()
            .eq(SignRecord::getStudentId, studentId)
            .eq(SignRecord::getStatus, STATUS_SIGNED));
        long absentCount = Math.max(totalCount - signedCount, 0);
        BigDecimal attendanceRate = totalCount == 0
            ? BigDecimal.ZERO
            : BigDecimal.valueOf(signedCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);

        StatDTO dto = new StatDTO();
        dto.setTotalCount(totalCount);
        dto.setSignedCount(signedCount);
        dto.setAbsentCount(absentCount);
        dto.setAttendanceRate(attendanceRate);
        return dto;
    }
}
