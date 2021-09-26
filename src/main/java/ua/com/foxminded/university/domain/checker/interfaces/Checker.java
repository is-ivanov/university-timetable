package ua.com.foxminded.university.domain.checker.interfaces;

import ua.com.foxminded.university.domain.entity.Lesson;

public interface Checker<T> {

    void check(T t, Lesson lesson);

}
