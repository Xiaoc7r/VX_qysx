package com.itheima.classroomsigninbackend.controller;

import com.itheima.classroomsigninbackend.common.Result;
import com.itheima.classroomsigninbackend.dto.StatDTO;
import com.itheima.classroomsigninbackend.service.StatService;
import com.itheima.classroomsigninbackend.utils.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sign/stat")
public class StatController {
    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @GetMapping("/personal")
    public Result<StatDTO> personalStat() {
        String studentId = UserContext.getUserId();
        StatDTO dto = statService.getPersonalStat(studentId);
        return Result.success(dto);
    }
}
