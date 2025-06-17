package com.example.smartcampusmanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/academic/admin")  // 修改路径前缀
public class CourseController {

    @GetMapping("/courses")
    public String getCourses() {
        return "Course list";
    }

    // 其他方法保持不变
}