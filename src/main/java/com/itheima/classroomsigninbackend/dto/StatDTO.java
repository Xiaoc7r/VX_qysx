package com.itheima.classroomsigninbackend.dto;

import java.math.BigDecimal;

public class StatDTO {
    private long totalCount;
    private long signedCount;
    private long absentCount;
    private BigDecimal attendanceRate;

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getSignedCount() {
        return signedCount;
    }

    public void setSignedCount(long signedCount) {
        this.signedCount = signedCount;
    }

    public long getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(long absentCount) {
        this.absentCount = absentCount;
    }

    public BigDecimal getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(BigDecimal attendanceRate) {
        this.attendanceRate = attendanceRate;
    }
}
