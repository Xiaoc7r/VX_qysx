package com.itheima.classroomsigninbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.classroomsigninbackend.entity.SignRecord;
import java.math.BigDecimal;

public interface SignRecordService extends IService<SignRecord> {
    void submitRecord(String studentId, String projectId, BigDecimal signLat, BigDecimal signLng);
}
