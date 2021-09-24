package ua.com.foxminded.university.domain.service.checker;

import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Teacher;

public class TeacherCheckerImpl implements TeacherChecker {
    @Override
    public boolean check(Teacher teacher, Lesson lesson) {
        return false;
    }
}
