package ua.com.foxminded.university.domain;

import java.util.List;

public class Timetable {

    private List<Lesson> lessons;

    public Timetable(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

}
