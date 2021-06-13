package ua.com.foxminded.university.domain.entity.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.Teacher;

public class LessonMapper implements RowMapper<Lesson> {

    private static final String ID = "id";
    private static final String TIME_START = "time_start";
    private static final String TIME_END = "time_end";
    private static final String TEACHER_ID = "teacher_id";
    private static final String TEACHER_FIRST_NAME = "teacher_first_name";
    private static final String TEACHER_LAST_NAME = "teacher_last_name";
    private static final String TEACHER_PATRONYMIC = "teacher_patronymic";
    private static final String DEPARTMENT_ID = "department_id";
    private static final String DEPARTMENT_NAME = "department_name";
    private static final String FACULTY_ID = "faculty_id";
    private static final String FACULTY_NAME = "faculty_name";
    private static final String COURSE_ID = "course_id";
    private static final String COURSE_NAME = "course_name";
    private static final String ROOM_ID = "room_id";
    private static final String BUILDING = "building";
    private static final String ROOM_NUMBER = "number";

    private StudentDao studentDao;

    @Autowired
    public LessonMapper(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    @Override
    public Lesson mapRow(ResultSet rs, int rowNum) throws SQLException {
        Lesson lesson = new Lesson();
        lesson.setId(rs.getInt(ID));
        lesson.setTeacher(createTeacher(rs));
        lesson.setCourse(createCourse(rs));
        lesson.setRoom(createRoom(rs));
        lesson.setTimeStart(rs.getTimestamp(TIME_START).toLocalDateTime());
        lesson.setTimeEnd(rs.getTimestamp(TIME_END).toLocalDateTime());
        lesson.setStudents(createStudents(lesson));
        return lesson;
    }

    private Teacher createTeacher(ResultSet rs) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setId(rs.getInt(TEACHER_ID));
        teacher.setFirstName(rs.getString(TEACHER_FIRST_NAME));
        teacher.setLastName(rs.getString(TEACHER_LAST_NAME));
        teacher.setPatronymic(rs.getString(TEACHER_PATRONYMIC));
        teacher.setDepartment(createDepartment(rs));
        return teacher;
    }

    private Department createDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt(DEPARTMENT_ID));
        department.setName(rs.getString(DEPARTMENT_NAME));
        department.setFaculty(createFaculty(rs));
        return department;
    }

    private Faculty createFaculty(ResultSet rs) throws SQLException {
        Faculty faculty = new Faculty();
        faculty.setId(rs.getInt(FACULTY_ID));
        faculty.setName(rs.getString(FACULTY_NAME));
        return faculty;
    }

    private Course createCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt(COURSE_ID));
        course.setName(rs.getString(COURSE_NAME));
        return course;
    }

    private Room createRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt(ROOM_ID));
        room.setBuilding(rs.getString(BUILDING));
        room.setNumber(rs.getString(ROOM_NUMBER));
        return room;
    }

    private List<Student> createStudents(Lesson lesson) {
        return studentDao.getAllForLesson(lesson);
    }

}
