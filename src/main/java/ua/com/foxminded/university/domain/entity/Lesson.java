package ua.com.foxminded.university.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {

    private int id;
    private Course course;
    private Teacher teacher;
    private Room room;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private List<Student> students;

}
