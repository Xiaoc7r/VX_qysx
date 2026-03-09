package com.itheima.classroomsigninbackend.controller;

import com.itheima.classroomsigninbackend.common.Result;
import com.itheima.classroomsigninbackend.entity.SignProject;
import com.itheima.classroomsigninbackend.service.SignProjectService;
import com.itheima.classroomsigninbackend.service.SignRecordService;
import com.itheima.classroomsigninbackend.utils.UserContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sign")
public class SignController {
    private final SignProjectService signProjectService;
    private final SignRecordService signRecordService;

    public SignController(SignProjectService signProjectService, SignRecordService signRecordService) {
        this.signProjectService = signProjectService;
        this.signRecordService = signRecordService;
    }

    @PostMapping("/project/create")
    public Result<Void> createProject(@RequestBody CreateProjectRequest request) {
        String teacherId = UserContext.getUserId();
        signProjectService.createProject(
            teacherId,
            request.getCourseId(),
            request.getProjectName(),
            request.getStartTime(),
            request.getEndTime(),
            request.getCenterLat(),
            request.getCenterLng(),
            request.getRadius()
        );
        return Result.success();
    }

    @GetMapping("/project/recent")
    public Result<List<SignProject>> recentProjects() {
        String userId = UserContext.getUserId();
        List<SignProject> projects = signProjectService.listRecentProjects(userId);
        return Result.success(projects);
    }

    @GetMapping("/project/list")
    public Result<List<SignProject>> listProjects(
        @RequestParam(value = "courseId", required = false) String courseId,
        @RequestParam(value = "status", required = false) String status) {
        List<SignProject> projects = signProjectService.listProjects(courseId, status);
        return Result.success(projects);
    }

    @PostMapping("/record/submit")
    public Result<Void> submitRecord(@RequestBody SubmitRecordRequest request) {
        String studentId = UserContext.getUserId();
        signRecordService.submitRecord(studentId, request.getProjectId(), request.getSignLat(), request.getSignLng());
        return Result.success();
    }

    public static class CreateProjectRequest {
        private String courseId;
        private String projectName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private BigDecimal centerLat;
        private BigDecimal centerLng;
        private Integer radius;

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        public LocalDateTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }

        public BigDecimal getCenterLat() {
            return centerLat;
        }

        public void setCenterLat(BigDecimal centerLat) {
            this.centerLat = centerLat;
        }

        public BigDecimal getCenterLng() {
            return centerLng;
        }

        public void setCenterLng(BigDecimal centerLng) {
            this.centerLng = centerLng;
        }

        public Integer getRadius() {
            return radius;
        }

        public void setRadius(Integer radius) {
            this.radius = radius;
        }
    }

    public static class SubmitRecordRequest {
        private String projectId;
        private BigDecimal signLat;
        private BigDecimal signLng;

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public BigDecimal getSignLat() {
            return signLat;
        }

        public void setSignLat(BigDecimal signLat) {
            this.signLat = signLat;
        }

        public BigDecimal getSignLng() {
            return signLng;
        }

        public void setSignLng(BigDecimal signLng) {
            this.signLng = signLng;
        }
    }
}
