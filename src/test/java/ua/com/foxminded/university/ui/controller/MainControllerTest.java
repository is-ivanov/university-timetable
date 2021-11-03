package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class MainControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MainController mainController = new MainController();
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(mainController)
            .setViewResolvers(viewResolver)
            .build();
    }

    @Test
    @DisplayName("Test indexPage")
    void testIndexPage() throws Exception {
        mockMvc.perform(get("/"))
            .andDo(print())
            .andExpect(view().name("index"))
            .andExpect(model().size(0));
    }
}