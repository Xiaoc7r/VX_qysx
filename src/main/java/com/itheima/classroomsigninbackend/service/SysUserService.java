package com.itheima.classroomsigninbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.classroomsigninbackend.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    String login(String username, String password);

    void register(String username, String password, String realName, String identifier, Integer role);
}
