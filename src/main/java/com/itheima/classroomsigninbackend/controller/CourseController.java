package com.itheima.classroomsigninbackend.controller;

import com.itheima.classroomsigninbackend.common.Result;
import com.itheima.classroomsigninbackend.entity.Course;
import com.itheima.classroomsigninbackend.entity.SysUser;
import com.itheima.classroomsigninbackend.service.CourseService;
import com.itheima.classroomsigninbackend.service.UserCourseService;
import com.itheima.classroomsigninbackend.utils.UserContext;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    private final CourseService courseService;
    private final UserCourseService userCourseService;

    public CourseController(CourseService courseService, UserCourseService userCourseService) {
        this.courseService = courseService;
        this.userCourseService = userCourseService;
    }

    @PostMapping("/create")
    public Result<Void> createCourse(@RequestBody CreateCourseRequest request) {
        String teacherId = UserContext.getUserId();
        courseService.createCourse(teacherId, request.getCourseName(), request.getCourseDesc());
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Course>> listCourses(@RequestParam("type") String type) {
        String userId = UserContext.getUserId();
        List<Course> courses = courseService.listCourses(type, userId);
        return Result.success(courses);
    }

    @PostMapping("/join/{courseId}")
    public Result<Void> joinCourse(@PathVariable String courseId) {
        String userId = UserContext.getUserId();
        userCourseService.joinCourse(userId, courseId);
        return Result.success();
    }

    @PostMapping("/leave/{courseId}")
    public Result<Void> leaveCourse(@PathVariable String courseId) {
        String userId = UserContext.getUserId();
        userCourseService.leaveCourse(userId, courseId);
        return Result.success();
    }

    @GetMapping("/{courseId}/students")
    public Result<List<SysUser>> listStudents(@PathVariable String courseId) {
        List<SysUser> students = userCourseService.listStudents(courseId);
        return Result.success(students);
    }

    public static class CreateCourseRequest {
        private String courseName;
        private String courseDesc;

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getCourseDesc() {
            return courseDesc;
        }

        public void setCourseDesc(String courseDesc) {
            this.courseDesc = courseDesc;
        }
    }
}
