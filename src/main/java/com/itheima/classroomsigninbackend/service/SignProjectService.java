package com.itheima.classroomsigninbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.classroomsigninbackend.entity.SignProject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SignProjectService extends IService<SignProject> {
    void createProject(String teacherId, String courseId, String projectName, LocalDateTime startTime,
                       LocalDateTime endTime, BigDecimal centerLat, BigDecimal centerLng, Integer radius);

    List<SignProject> listRecentProjects(String userId);

    List<SignProject> listProjects(String courseId, String status);
}
