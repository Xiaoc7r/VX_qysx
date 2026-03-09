package com.itheima.classroomsigninbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.classroomsigninbackend.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
