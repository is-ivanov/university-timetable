package ua.com.foxminded.university.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Lesson {

    private int id;
    private Teacher teacher;
    private List<Student> students;
    private Course course;
    private Room room;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;

}
