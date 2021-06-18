package ua.com.foxminded.university.dao.interfaces;

import java.util.List;

import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;

public interface StudentDao extends Dao<Student> {

    List<Student> getAllForLesson(Lesson lesson);

}
