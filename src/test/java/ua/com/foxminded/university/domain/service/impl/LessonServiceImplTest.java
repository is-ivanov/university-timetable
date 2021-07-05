package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Teacher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceImplTest {

    private LessonServiceImpl lessonService;

    @Mock
    private LessonDao lessonDaoMock;

    @BeforeEach
    void setUp(){
        lessonService = new LessonServiceImpl(lessonDaoMock);
    }

    @Nested
    @DisplayName("test 'add' method")
    class addTest{

        @Test
        @DisplayName("should call Dao.add once")
        void testAdd_CallDaoOnce() throws Exception {
            Teacher teacher = new Teacher();
            teacher.setId(1);
            Course course = new Course();
            course.setId(1);
            Room room = new Room();
            room.setId(2);


            Lesson lesson = new Lesson();
            lesson.setTeacher(new Teacher());
            lesson.setRoom(new Room());
            when(lessonDaoMock.getAllByTeacher(0)).thenReturn(null);
            lessonService.add(lesson);
            verify(lessonDaoMock).add(lesson);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class updateTest{

    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest{

    }

    @Nested
    @DisplayName("test 'getAll' method")
    class getAllTest{

    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest{

    }
}