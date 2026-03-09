package com.itheima.classroomsigninbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.classroomsigninbackend.common.ResultCodeEnum;
import com.itheima.classroomsigninbackend.entity.SysUser;
import com.itheima.classroomsigninbackend.exception.BusinessException;
import com.itheima.classroomsigninbackend.mapper.SysUserMapper;
import com.itheima.classroomsigninbackend.service.SysUserService;
import com.itheima.classroomsigninbackend.utils.JwtUtils;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Override
    public String login(String username, String password) {
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getUsername, username));
        if (user == null || user.getPassword() == null || !user.getPassword().equals(password)) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED.getCode(), "Invalid username or password");
        }
        return JwtUtils.generateToken(user.getId());
    }

    @Override
    public void register(String username, String password, String realName, String identifier, Integer role) {
        long count = count(new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getUsername, username));
        if (count > 0) {
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR.getCode(), "Username already exists");
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setIdentifier(identifier);
        user.setRole(role);
        save(user);
    }
}
