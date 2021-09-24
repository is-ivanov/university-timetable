package ua.com.foxminded.university.domain.service.checker;

import ua.com.foxminded.university.domain.entity.Lesson;

public interface Checker<T> {

    boolean check(T t, Lesson lesson);

}
