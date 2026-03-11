package com.itheima.classroomsigninbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.classroomsigninbackend.entity.Course;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
