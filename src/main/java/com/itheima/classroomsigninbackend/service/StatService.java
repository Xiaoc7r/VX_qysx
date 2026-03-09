package com.itheima.classroomsigninbackend.service;

import com.itheima.classroomsigninbackend.dto.StatDTO;

public interface StatService {
    StatDTO getPersonalStat(String studentId);
}
