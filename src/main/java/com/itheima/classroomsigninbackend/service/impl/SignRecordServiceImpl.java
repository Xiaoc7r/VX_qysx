package com.itheima.classroomsigninbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.classroomsigninbackend.common.ResultCodeEnum;
import com.itheima.classroomsigninbackend.entity.SignProject;
import com.itheima.classroomsigninbackend.entity.SignRecord;
import com.itheima.classroomsigninbackend.exception.BusinessException;
import com.itheima.classroomsigninbackend.mapper.SignProjectMapper;
import com.itheima.classroomsigninbackend.mapper.SignRecordMapper;
import com.itheima.classroomsigninbackend.service.SignRecordService;
import com.itheima.classroomsigninbackend.utils.LocationUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class SignRecordServiceImpl extends ServiceImpl<SignRecordMapper, SignRecord> implements SignRecordService {
    private static final int STATUS_SIGNED = 1;

    private final SignProjectMapper signProjectMapper;

    public SignRecordServiceImpl(SignProjectMapper signProjectMapper) {
        this.signProjectMapper = signProjectMapper;
    }

    @Override
    public void submitRecord(String studentId, String projectId, BigDecimal signLat, BigDecimal signLng) {
        if (studentId == null || studentId.isBlank()) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }
        long existed = count(new LambdaQueryWrapper<SignRecord>()
            .eq(SignRecord::getProjectId, projectId)
            .eq(SignRecord::getStudentId, studentId));
        if (existed > 0) {
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR.getCode(), "Already signed");
        }
        SignProject project = signProjectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR.getCode(), "Project not found");
        }
        LocalDateTime now = LocalDateTime.now();
        if (project.getStartTime() != null && now.isBefore(project.getStartTime())) {
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR.getCode(), "Not started");
        }
        if (project.getEndTime() != null && now.isAfter(project.getEndTime())) {
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR.getCode(), "Already ended");
        }
        double distance = LocationUtils.distanceMeters(
            project.getCenterLat().doubleValue(),
            project.getCenterLng().doubleValue(),
            signLat.doubleValue(),
            signLng.doubleValue()
        );
        if (project.getRadius() != null && distance > project.getRadius()) {
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR.getCode(), "Too far from center");
        }
        SignRecord record = new SignRecord();
        record.setProjectId(projectId);
        record.setStudentId(studentId);
        record.setSignTime(now);
        record.setSignLat(signLat);
        record.setSignLng(signLng);
        record.setStatus(STATUS_SIGNED);
        save(record);
    }
}
