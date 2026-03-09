package com.itheima.classroomsigninbackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.classroomsigninbackend.common.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("sign_record")
public class SignRecord extends BaseEntity {
    @TableField("project_id")
    private String projectId;

    @TableField("student_id")
    private String studentId;

    @TableField("sign_time")
    private LocalDateTime signTime;

    @TableField("sign_lat")
    private BigDecimal signLat;

    @TableField("sign_lng")
    private BigDecimal signLng;

    @TableField("status")
    private Integer status;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public LocalDateTime getSignTime() {
        return signTime;
    }

    public void setSignTime(LocalDateTime signTime) {
        this.signTime = signTime;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
