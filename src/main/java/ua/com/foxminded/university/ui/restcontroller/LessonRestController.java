package ua.com.foxminded.university.ui.restcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.mapper.LessonDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/lessons/api/")
public class LessonRestController {

    private final LessonService lessonService;
    private final LessonDtoMapper lessonDtoMapper;

    @GetMapping("/{id}")
    public LessonDto showLesson(@PathVariable("id") int lessonId) {
        log.debug("Getting lesson id({})", lessonId);
        Lesson lesson = lessonService.getById(lessonId);
        log.info("Found lesson [teacher {}, course {}, room {}]",
            lesson.getTeacher().getFullName(), lesson.getCourse().getName(),
            lesson.getRoom().getNumber());
        return lessonDtoMapper.lessonToLessonDto(lesson);
    }
}
