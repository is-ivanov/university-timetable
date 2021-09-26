package ua.com.foxminded.university.domain.checker.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.checker.interfaces.LessonChecker;
import ua.com.foxminded.university.domain.checker.interfaces.RoomChecker;
import ua.com.foxminded.university.domain.checker.interfaces.TeacherChecker;

@Slf4j
@AllArgsConstructor
@Component
public class LessonCheckerImpl implements LessonChecker {

    private final TeacherChecker teacherChecker;
    private final RoomChecker roomChecker;

    @Override
    public void check(Lesson lesson) {
        log.debug("Start checking the lesson id({})", lesson.getId());
        Teacher teacher = lesson.getTeacher();
        teacherChecker.check(teacher, lesson);
        log.info("Teacher id({}) is available", teacher.getId());
        Room room = lesson.getRoom();
        roomChecker.check(room,lesson);
        log.info("Room id({}) is available", room.getId());
        log.info("Lesson id({}) checking passed", lesson.getId());
    }
}
