package ua.com.foxminded.university;

import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class TestObjects {
    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final String NAME_FIRST_FACULTY = "IT faculty";
    public static final String NAME_SECOND_FACULTY = "Chemistry faculty";
    public static final String NAME_FIRST_GROUP = "99XT-1";
    public static final String NAME_SECOND_GROUP = "56FDS";
    public static final String NAME_FIRST_DEPARTMENT = "Java department";
    public static final String NAME_SECOND_DEPARTMENT = "C# department";
    public static final String NAME_FIRST_COURSE = "Java";
    public static final String NAME_SECOND_COURSE = "English";
    public static final String BUILDING_FIRST_ROOM = "building-1";
    public static final String NUMBER_FIRST_ROOM = "1457a";
    public static final String BUILDING_AND_NUMBER_FIRST_ROOM = "building-1 - 1457a";
    public static final String BUILDING_SECOND_ROOM = "building-2";
    public static final String NUMBER_SECOND_ROOM = "101";
    public static final String NAME_FIRST_STUDENT = "Mike";
    public static final String PATRONYMIC_FIRST_STUDENT = "Jr";
    public static final String LAST_NAME_FIRST_STUDENT = "Smith";
    public static final String NAME_SECOND_STUDENT = "Alan";
    public static final String PATRONYMIC_SECOND_STUDENT = "III";
    public static final String LAST_NAME_SECOND_STUDENT = "Johnson";
    public static final String NAME_FIRST_TEACHER = "Ivan";
    public static final String PATRONYMIC_FIRST_TEACHER = "Petrovich";
    public static final String LAST_NAME_FIRST_TEACHER = "Ivanov";
    public static final String FULL_NAME_FIRST_TEACHER = "Ivanov I.P.";
    public static final String NAME_SECOND_TEACHER = "Oleg";
    public static final String PATRONYMIC_SECOND_TEACHER = "Ivanovich";
    public static final String LAST_NAME_SECOND_TEACHER = "Petrov";
    public static final String NAME_THIRD_STUDENT = "Peter";
    public static final LocalDateTime DATE_START_FIRST_LESSON =
        LocalDateTime.of(2021, 8, 10, 8, 0);
    public static final LocalDateTime DATE_END_FIRST_LESSON =
        LocalDateTime.of(2021, 8, 10, 9, 30);
    public static final LocalDateTime DATE_START_SECOND_LESSON =
        LocalDateTime.of(2021, 10, 4, 12, 15);
    public static final LocalDateTime DATE_END_SECOND_LESSON =
        LocalDateTime.of(2021, 10, 4, 13, 45);

    public static Faculty createTestFaculty() {
        return new Faculty(ID1, NAME_FIRST_FACULTY);
    }

    public static Faculty createTestFaculty(int facultyId) {
        return new Faculty(facultyId, NAME_FIRST_FACULTY);
    }

    public static List<Faculty> createTestFaculties() {
        Faculty faculty1 = new Faculty(ID1, NAME_FIRST_FACULTY);
        Faculty faculty2 = new Faculty(ID2, NAME_SECOND_FACULTY);
        return Arrays.asList(faculty1, faculty2);
    }

    public static Course createTestCourse() {
        return new Course(ID1, NAME_FIRST_COURSE);
    }

    public static List<Course> createTestCourses() {
        Course course1 = new Course(ID1, NAME_FIRST_COURSE);
        Course course2 = new Course(ID2, NAME_SECOND_COURSE);
        return Arrays.asList(course1, course2);
    }

    public static Group createTestGroup() {
        return new Group(ID1, NAME_FIRST_GROUP, createTestFaculty(), true);
    }

    public static Group createTestGroup(int facultyId) {
        return new Group(ID1, NAME_FIRST_GROUP, createTestFaculty(facultyId), true);
    }

    public static Group createTestGroup(int facultyId, int groupId) {
        return new Group(groupId, NAME_FIRST_GROUP, createTestFaculty(facultyId), true);
    }

    public static List<Group> createTestGroups() {
        Faculty faculty = createTestFaculty();
        Group group1 = new Group(ID1, NAME_FIRST_GROUP, faculty, true);
        Group group2 = new Group(ID2, NAME_SECOND_GROUP, faculty, true);
        return Arrays.asList(group1, group2);
    }

    public static List<Group> createTestGroups(int facultyId) {
        Faculty faculty = createTestFaculty(facultyId);
        Group group1 = new Group(ID1, NAME_FIRST_GROUP, faculty, true);
        Group group2 = new Group(ID2, NAME_SECOND_GROUP, faculty, true);
        return Arrays.asList(group1, group2);
    }

    public static Department createTestDepartment(int facultyId) {
        return new Department(ID1, NAME_FIRST_DEPARTMENT, createTestFaculty(facultyId));
    }

    public static List<Department> createTestDepartments() {
        Faculty faculty = createTestFaculty();
        Department department1 = new Department(ID1, NAME_FIRST_DEPARTMENT, faculty);
        Department department2 = new Department(ID2, NAME_SECOND_DEPARTMENT, faculty);
        return Arrays.asList(department1, department2);
    }

    public static List<Department> createTestDepartments(int facultyId) {
        Faculty faculty = createTestFaculty(facultyId);
        Department department1 = new Department(ID1, NAME_FIRST_DEPARTMENT, faculty);
        Department department2 = new Department(ID2, NAME_SECOND_DEPARTMENT, faculty);
        return Arrays.asList(department1, department2);
    }

    public static List<Student> createTestStudents() {
        Group testGroup = createTestGroup();
        Student student1 = Student.builder()
            .id(ID1)
            .firstName(NAME_FIRST_STUDENT)
            .patronymic(PATRONYMIC_FIRST_STUDENT)
            .lastName(LAST_NAME_FIRST_STUDENT)
            .group(testGroup)
            .active(true)
            .build();
        Student student2 = Student.builder()
            .id(ID2)
            .firstName(NAME_SECOND_STUDENT)
            .patronymic(PATRONYMIC_SECOND_STUDENT)
            .lastName(LAST_NAME_SECOND_STUDENT)
            .group(testGroup)
            .active(true)
            .build();

        return Arrays.asList(student1, student2);
    }

    public static List<StudentDto> createTestStudentDtos(int groupId) {
        StudentDto studentDto1 = StudentDto.builder()
            .id(ID1)
            .firstName(NAME_FIRST_STUDENT)
            .patronymic(PATRONYMIC_FIRST_STUDENT)
            .lastName(LAST_NAME_FIRST_STUDENT)
            .active(true)
            .groupId(groupId)
            .groupName(NAME_FIRST_GROUP)
            .build();
        StudentDto studentDto2 = StudentDto.builder()
            .id(ID2)
            .firstName(NAME_SECOND_STUDENT)
            .patronymic(PATRONYMIC_SECOND_STUDENT)
            .lastName(LAST_NAME_SECOND_STUDENT)
            .active(false)
            .groupId(groupId)
            .groupName(NAME_FIRST_GROUP)
            .build();

        return Arrays.asList(studentDto1, studentDto2);
    }

    public static Teacher createTestTeacher() {
        return Teacher.builder()
            .id(ID1)
            .firstName(NAME_FIRST_TEACHER)
            .patronymic(PATRONYMIC_FIRST_TEACHER)
            .lastName(LAST_NAME_FIRST_TEACHER)
            .department(createTestDepartment(ID1))
            .active(true)
            .build();
    }

    public static List<Teacher> createTestTeachers(int facultyId) {
        Department testDepartment = createTestDepartment(facultyId);
        Teacher teacher1 = Teacher.builder()
            .id(ID1)
            .firstName(NAME_FIRST_TEACHER)
            .patronymic(PATRONYMIC_FIRST_TEACHER)
            .lastName(LAST_NAME_FIRST_TEACHER)
            .department(testDepartment)
            .active(true)
            .build();
        Teacher teacher2 = Teacher.builder()
            .id(ID2)
            .firstName(NAME_SECOND_TEACHER)
            .patronymic(PATRONYMIC_SECOND_TEACHER)
            .lastName(LAST_NAME_SECOND_TEACHER)
            .department(testDepartment)
            .active(true)
            .build();

        return Arrays.asList(teacher1, teacher2);
    }

    public static List<TeacherDto> createTestTeacherDtos(int facultyId) {
        Department testDepartment = createTestDepartment(facultyId);
        TeacherDto teacherDto1 = TeacherDto.builder()
            .id(ID1)
            .firstName(NAME_FIRST_TEACHER)
            .patronymic(PATRONYMIC_FIRST_TEACHER)
            .lastName(LAST_NAME_FIRST_TEACHER)
            .departmentId(testDepartment.getId())
            .departmentName(testDepartment.getName())
            .active(true)
            .build();
        TeacherDto teacherDto2 = TeacherDto.builder()
            .id(ID2)
            .firstName(NAME_SECOND_TEACHER)
            .patronymic(PATRONYMIC_SECOND_TEACHER)
            .lastName(LAST_NAME_SECOND_TEACHER)
            .departmentId(testDepartment.getId())
            .departmentName(testDepartment.getName())
            .active(true)
            .build();

        return Arrays.asList(teacherDto1, teacherDto2);
    }

    public static Room createTestRoom() {
        return new Room(ID1, BUILDING_FIRST_ROOM, NUMBER_FIRST_ROOM);
    }

    public static List<Room> createTestRooms() {
        Room room1 = new Room(ID1, BUILDING_FIRST_ROOM, NUMBER_FIRST_ROOM);
        Room room2 = new Room(ID2, BUILDING_SECOND_ROOM, NUMBER_SECOND_ROOM);
        return Arrays.asList(room1, room2);
    }

    public static List<Lesson> createTestLessons() {
        Lesson lesson1 = Lesson.builder()
            .id(ID1)
            .course(createTestCourse())
            .teacher(createTestTeacher())
            .room(createTestRoom())
            .timeStart(DATE_START_FIRST_LESSON)
            .timeEnd(DATE_END_FIRST_LESSON)
            .students(createTestStudents())
            .build();
        Lesson lesson2 = Lesson.builder()
            .id(ID2)
            .course(createTestCourse())
            .teacher(createTestTeacher())
            .room(createTestRoom())
            .timeStart(DATE_START_SECOND_LESSON)
            .timeEnd(DATE_END_SECOND_LESSON)
            .students(createTestStudents())
            .build();
        return Arrays.asList(lesson1, lesson2);
    }

    public static List<LessonDto> createTestLessonDtos() {
        LessonDto lessonDto1 = LessonDto.builder()
            .id(ID1)
            .courseId(ID1)
            .courseName(NAME_FIRST_COURSE)
            .teacherId(ID1)
            .teacherFullName(FULL_NAME_FIRST_TEACHER)
            .roomId(ID1)
            .buildingAndRoom(BUILDING_AND_NUMBER_FIRST_ROOM)
            .timeStart(DATE_START_FIRST_LESSON)
            .timeEnd(DATE_END_FIRST_LESSON)
            .students(createTestStudentDtos(ID2))
            .build();
        LessonDto lessonDto2 = LessonDto.builder()
            .id(ID2)
            .courseId(ID1)
            .courseName(NAME_FIRST_COURSE)
            .teacherId(ID1)
            .teacherFullName(FULL_NAME_FIRST_TEACHER)
            .roomId(ID1)
            .buildingAndRoom(BUILDING_AND_NUMBER_FIRST_ROOM)
            .timeStart(DATE_START_SECOND_LESSON)
            .timeEnd(DATE_END_SECOND_LESSON)
            .students(createTestStudentDtos(ID2))
            .build();
        return Arrays.asList(lessonDto1, lessonDto2);
    }
}
