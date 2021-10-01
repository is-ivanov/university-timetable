package ua.com.foxminded.university.ui.controller;

import org.springframework.test.web.servlet.MockMvc;

public class CourseRequestBuilder {

    private final MockMvc mockMvc;

    public CourseRequestBuilder(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }
}
