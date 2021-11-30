package ua.com.foxminded.university;

import ua.com.foxminded.university.domain.dto.*;
import ua.com.foxminded.university.domain.entity.*;
import ua.com.foxminded.university.domain.filter.LessonFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TestObjects {
    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final int ID3 = 3;
    public static final int FACULTY_ID1 = 10;
    public static final int FACULTY_ID2 = 4;
    public static final int DEPARTMENT_ID1 = 8;
    public static final int DEPARTMENT_ID2 = 54;
    public static final int TEACHER_ID1 = 7;
    public static final int TEACHER_ID2 = 78;
    public static final int TEACHER_ID3 = 12;
    public static final int STUDENT_ID1 = 12;
    public static final int STUDENT_ID2 = 78;
    public static final int STUDENT_ID3 = 3;
    public static final int COURSE_ID1 = 45;
    public static final int COURSE_ID2 = 13;
    public static final int ROOM_ID1 = 5;
    public static final int ROOM_ID2 = 62;
    public static final int LESSON_ID1 = 23;
    public static final int LESSON_ID2 = 2;
    public static final int LESSON_ID3 = 75;
    public static final int GROUP_ID1 = 2;
    public static final int GROUP_ID2 = 14;
    public static final String NAME_FIRST_FACULTY = "IT faculty";
    public static final String NAME_SECOND_FACULTY = "Chemistry faculty";
    public static final String NAME_THIRD_FACULTY = "Faculty name";
    public static final String NAME_FIRST_GROUP = "99XT-1";
    public static final String NAME_SECOND_GROUP = "56FDS";
    public static final String NAME_THIRD_GROUP = "21Ger-1";
    public static final String NAME_FIRST_DEPARTMENT = "Java department";
    public static final String NAME_SECOND_DEPARTMENT = "C# department";
    public static final String NAME_THIRD_DEPARTMENT = "JavaScript department";
    public static final String NAME_FIRST_COURSE = "Java";
    public static final String NAME_SECOND_COURSE = "English";
    public static final String NAME_THIRD_COURSE = "Chemistry";
    public static final String BUILDING_FIRST_ROOM = "building-1";
    public static final String NUMBER_FIRST_ROOM = "1457a";
    public static final String BUILDING_AND_NUMBER_FIRST_ROOM = "building-1 - 1457a";
    public static final String BUILDING_SECOND_ROOM = "building-2";
    public static final String NUMBER_SECOND_ROOM = "812b";
    public static final String BUILDING_THIRD_ROOM = "building-2";
    public static final String NUMBER_THIRD_ROOM = "145";
    public static final String NAME_FIRST_STUDENT = "Mike";
    public static final String PATRONYMIC_FIRST_STUDENT = "Jr";
    public static final String LAST_NAME_FIRST_STUDENT = "Smith";
    public static final String FULL_NAME_FIRST_STUDENT = "Smith, M.J.";
    public static final String NAME_SECOND_STUDENT = "Alan";
    public static final String PATRONYMIC_SECOND_STUDENT = "III";
    public static final String LAST_NAME_SECOND_STUDENT = "Johnson";
    public static final String FULL_NAME_SECOND_STUDENT = "Johnson, A.I.";
    public static final String NAME_THIRD_STUDENT = "Peter";
    public static final String PATRONYMIC_THIRD_STUDENT = "Dre";
    public static final String LAST_NAME_THIRD_STUDENT = "Daddy";
    public static final String NAME_FIRST_TEACHER = "Ivan";
    public static final String PATRONYMIC_FIRST_TEACHER = "Petrovich";
    public static final String LAST_NAME_FIRST_TEACHER = "Ivanov";
    public static final String FULL_NAME_FIRST_TEACHER = "Ivanov, I.P.";
    public static final String NAME_SECOND_TEACHER = "Oleg";
    public static final String PATRONYMIC_SECOND_TEACHER = "Ivanovich";
    public static final String LAST_NAME_SECOND_TEACHER = "Petrov";
    public static final String NAME_THIRD_TEACHER = "John";
    public static final String PATRONYMIC_THIRD_TEACHER = "Jr";
    public static final String LAST_NAME_THIRD_TEACHER = "Thompson";
    public static final LocalDateTime DATE_START_FIRST_LESSON =
        LocalDateTime.of(2021, 5, 10, 10, 0);
    public static final String TEXT_DATE_START_FIRST_LESSON = "2021-05-10 10:00";
    public static final LocalDateTime DATE_END_FIRST_LESSON =
        LocalDateTime.of(2021, 5, 10, 11, 30);
    public static final String TEXT_DATE_END_FIRST_LESSON = "2021-05-10 11:30";
    public static final LocalDateTime DATE_START_SECOND_LESSON =
        LocalDateTime.of(2021, 10, 4, 12, 15);
    public static final String TEXT_DATE_START_SECOND_LESSON = "2021-10-04 12:15";
    public static final LocalDateTime DATE_END_SECOND_LESSON =
        LocalDateTime.of(2021, 10, 4, 13, 45);
    public static final String TEXT_DATE_END_SECOND_LESSON = "2021-10-04 13:45";
    public static final LocalDateTime DATE_FROM = LocalDateTime.of(2021, 8, 10, 8, 0);
    public static final String TEXT_DATE_FROM = "2021-08-10 08:00";
    public static final LocalDateTime DATE_TO = LocalDateTime.of(2021, 9, 15, 23, 0);
    public static final String TEXT_DATE_TO = "2021-09-15 23:00";

    public static Faculty createTestFaculty() {
        return new Faculty(FACULTY_ID1, NAME_FIRST_FACULTY);
    }

    public static Faculty createTestSecondFaculty() {
        return new Faculty(FACULTY_ID2, NAME_SECOND_FACULTY);
    }

    public static Faculty createTestFaculty(int facultyId) {
        return new Faculty(facultyId, NAME_FIRST_FACULTY);
    }

    public static List<Faculty> createTestFaculties() {
        Faculty faculty1 = new Faculty(FACULTY_ID1, NAME_FIRST_FACULTY);
        Faculty faculty2 = new Faculty(FACULTY_ID2, NAME_SECOND_FACULTY);
        return new ArrayList<>(Arrays.asList(faculty1, faculty2));
    }

    public static Course createTestCourse() {
        return new Course(COURSE_ID1, NAME_FIRST_COURSE);
    }

    public static List<Course> createTestCourses() {
        Course course1 = new Course(COURSE_ID1, NAME_FIRST_COURSE);
        Course course2 = new Course(COURSE_ID2, NAME_SECOND_COURSE);
        return new ArrayList<>(Arrays.asList(course1, course2));
    }

    public static Group createTestGroup() {
        return new Group(GROUP_ID1, NAME_FIRST_GROUP, createTestFaculty(), true);
    }

    public static Group createTestSecondGroup() {
        return new Group(GROUP_ID2, NAME_SECOND_GROUP, createTestSecondFaculty(),
            true);
    }

    public static GroupDto createTestGroupDto() {
        return new GroupDto(GROUP_ID1, NAME_FIRST_GROUP,true,
            FACULTY_ID1, NAME_FIRST_FACULTY);
    }

    public static List<Group> createTestGroups() {
        Faculty faculty = createTestFaculty();
        Group group1 = new Group(GROUP_ID1, NAME_FIRST_GROUP, faculty, true);
        Group group2 = new Group(GROUP_ID2, NAME_SECOND_GROUP, faculty, true);
        return new ArrayList<>(Arrays.asList(group1, group2));
    }

    public static List<Group> createTestGroups(int facultyId) {
        Faculty faculty = createTestFaculty(facultyId);
        Group group1 = new Group(GROUP_ID1, NAME_FIRST_GROUP, faculty, true);
        Group group2 = new Group(GROUP_ID2, NAME_SECOND_GROUP, faculty, true);
        return new ArrayList<>(Arrays.asList(group1, group2));
    }

    public static Department createTestDepartment(int facultyId) {
        return new Department(DEPARTMENT_ID1, NAME_FIRST_DEPARTMENT, createTestFaculty(facultyId));
    }

    public static DepartmentDto createTestDepartmentDto() {
        return new DepartmentDto(DEPARTMENT_ID1, NAME_FIRST_DEPARTMENT,
            FACULTY_ID1, NAME_FIRST_FACULTY);
    }

    public static List<Department> createTestDepartments() {
        Faculty faculty = createTestFaculty();
        Department department1 = new Department(DEPARTMENT_ID1, NAME_FIRST_DEPARTMENT, faculty);
        Department department2 = new Department(DEPARTMENT_ID2, NAME_SECOND_DEPARTMENT, faculty);
        return new ArrayList<>(Arrays.asList(department1, department2));
    }

    public static List<Department> createTestDepartments(int facultyId) {
        Faculty faculty = createTestFaculty(facultyId);
        Department department1 = new Department(DEPARTMENT_ID1, NAME_FIRST_DEPARTMENT, faculty);
        Department department2 = new Department(DEPARTMENT_ID2, NAME_SECOND_DEPARTMENT, faculty);
        return new ArrayList<>(Arrays.asList(department1, department2));
    }

    public static Student createTestStudent() {
        Group testGroup = createTestGroup();
        return Student.builder()
            .id(STUDENT_ID1)
            .firstName(NAME_FIRST_STUDENT)
            .patronymic(PATRONYMIC_FIRST_STUDENT)
            .lastName(LAST_NAME_FIRST_STUDENT)
            .group(testGroup)
            .active(true)
            .build();
    }

    public static List<Student> createTestStudents() {
        Group testGroup = createTestGroup();
        Student student1 = createTestStudent();
        Student student2 = Student.builder()
            .id(STUDENT_ID2)
            .firstName(NAME_SECOND_STUDENT)
            .patronymic(PATRONYMIC_SECOND_STUDENT)
            .lastName(LAST_NAME_SECOND_STUDENT)
            .group(testGroup)
            .active(true)
            .build();

        return new ArrayList<>(Arrays.asList(student1, student2));
    }

    public static StudentDto createTestStudentDto() {
        return StudentDto.builder()
            .id(STUDENT_ID1)
            .firstName(NAME_FIRST_STUDENT)
            .patronymic(PATRONYMIC_FIRST_STUDENT)
            .lastName(LAST_NAME_FIRST_STUDENT)
            .fullName(FULL_NAME_FIRST_STUDENT)
            .active(true)
            .groupId(GROUP_ID1)
            .groupName(NAME_FIRST_GROUP)
            .build();
    }

    public static StudentDto createTestStudentDto(int groupId) {
        return StudentDto.builder()
            .id(STUDENT_ID1)
            .firstName(NAME_FIRST_STUDENT)
            .patronymic(PATRONYMIC_FIRST_STUDENT)
            .lastName(LAST_NAME_FIRST_STUDENT)
            .fullName(FULL_NAME_FIRST_STUDENT)
            .active(true)
            .groupId(groupId)
            .groupName(NAME_FIRST_GROUP)
            .build();
    }

    public static List<StudentDto> createTestStudentDtos(int groupId) {
        StudentDto studentDto1 = createTestStudentDto(groupId);
        StudentDto studentDto2 = StudentDto.builder()
            .id(STUDENT_ID2)
            .firstName(NAME_SECOND_STUDENT)
            .patronymic(PATRONYMIC_SECOND_STUDENT)
            .lastName(LAST_NAME_SECOND_STUDENT)
            .fullName(FULL_NAME_SECOND_STUDENT)
            .active(false)
            .groupId(groupId)
            .groupName(NAME_FIRST_GROUP)
            .build();

        return new ArrayList<>(Arrays.asList(studentDto1, studentDto2));
    }

    public static Teacher createTestTeacher() {
        return Teacher.builder()
            .id(TEACHER_ID1)
            .firstName(NAME_FIRST_TEACHER)
            .patronymic(PATRONYMIC_FIRST_TEACHER)
            .lastName(LAST_NAME_FIRST_TEACHER)
            .department(createTestDepartment(FACULTY_ID1))
            .active(true)
            .build();
    }

    public static List<Teacher> createTestTeachers(int facultyId) {
        Department testDepartment = createTestDepartment(facultyId);
        Teacher teacher1 = Teacher.builder()
            .id(TEACHER_ID1)
            .firstName(NAME_FIRST_TEACHER)
            .patronymic(PATRONYMIC_FIRST_TEACHER)
            .lastName(LAST_NAME_FIRST_TEACHER)
            .department(testDepartment)
            .active(true)
            .build();
        Teacher teacher2 = Teacher.builder()
            .id(TEACHER_ID2)
            .firstName(NAME_SECOND_TEACHER)
            .patronymic(PATRONYMIC_SECOND_TEACHER)
            .lastName(LAST_NAME_SECOND_TEACHER)
            .department(testDepartment)
            .active(true)
            .build();

        return new ArrayList<>(Arrays.asList(teacher1, teacher2));
    }

    public static TeacherDto createTestTeacherDto() {
        return TeacherDto.builder()
            .id(TEACHER_ID1)
            .firstName(NAME_FIRST_TEACHER)
            .patronymic(PATRONYMIC_FIRST_TEACHER)
            .lastName(LAST_NAME_FIRST_TEACHER)
            .fullName(FULL_NAME_FIRST_TEACHER)
            .departmentId(DEPARTMENT_ID1)
            .departmentName(NAME_FIRST_DEPARTMENT)
            .active(true)
            .build();
    }

    public static List<TeacherDto> createTestTeacherDtos(int facultyId) {
        Department testDepartment = createTestDepartment(facultyId);
        TeacherDto teacherDto1 = TeacherDto.builder()
            .id(TEACHER_ID1)
            .firstName(NAME_FIRST_TEACHER)
            .patronymic(PATRONYMIC_FIRST_TEACHER)
            .lastName(LAST_NAME_FIRST_TEACHER)
            .departmentId(testDepartment.getId())
            .departmentName(testDepartment.getName())
            .active(true)
            .build();
        TeacherDto teacherDto2 = TeacherDto.builder()
            .id(TEACHER_ID2)
            .firstName(NAME_SECOND_TEACHER)
            .patronymic(PATRONYMIC_SECOND_TEACHER)
            .lastName(LAST_NAME_SECOND_TEACHER)
            .departmentId(testDepartment.getId())
            .departmentName(testDepartment.getName())
            .active(true)
            .build();

        return new ArrayList<>(Arrays.asList(teacherDto1, teacherDto2));
    }

    public static Room createTestRoom() {
        return new Room(ROOM_ID1, BUILDING_FIRST_ROOM, NUMBER_FIRST_ROOM);
    }

    public static List<Room> createTestRooms() {
        Room room1 = createTestRoom();
        Room room2 = new Room(ROOM_ID2, BUILDING_SECOND_ROOM, NUMBER_SECOND_ROOM);
        return new ArrayList<>(Arrays.asList(room1, room2));
    }

    public static Lesson createTestLesson() {
        return Lesson.builder()
            .course(createTestCourse())
            .teacher(createTestTeacher())
            .room(createTestRoom())
            .timeStart(DATE_START_FIRST_LESSON)
            .timeEnd(DATE_END_FIRST_LESSON)
            .students(new HashSet<>(createTestStudents()))
            .build();
    }

    public static Lesson createTestLesson(int lessonId) {
        return Lesson.builder()
            .id(lessonId)
            .course(createTestCourse())
            .teacher(createTestTeacher())
            .room(createTestRoom())
            .timeStart(DATE_START_FIRST_LESSON)
            .timeEnd(DATE_END_FIRST_LESSON)
            .students(new HashSet<>(createTestStudents()))
            .build();
    }

    public static List<Lesson> createTestLessons() {
        Lesson lesson1 = createTestLesson(LESSON_ID1);
        Lesson lesson2 = Lesson.builder()
            .id(LESSON_ID2)
            .course(createTestCourse())
            .teacher(createTestTeacher())
            .room(createTestRoom())
            .timeStart(DATE_START_SECOND_LESSON)
            .timeEnd(DATE_END_SECOND_LESSON)
            .students(new HashSet<>(createTestStudents()))
            .build();
        return new ArrayList<>(Arrays.asList(lesson1, lesson2));
    }

    public static LessonDto createTestLessonDto(int lessonId) {
        return LessonDto.builder()
            .id(lessonId)
            .courseId(COURSE_ID1)
            .courseName(NAME_FIRST_COURSE)
            .teacherId(TEACHER_ID1)
            .teacherFullName(FULL_NAME_FIRST_TEACHER)
            .roomId(ROOM_ID1)
            .buildingAndRoom(BUILDING_AND_NUMBER_FIRST_ROOM)
            .timeStart(DATE_START_FIRST_LESSON)
            .timeEnd(DATE_END_FIRST_LESSON)
            .students(new HashSet<>(createTestStudentDtos(ID2)))
            .build();
    }

    public static List<LessonDto> createTestLessonDtos() {
        LessonDto lessonDto1 = createTestLessonDto(LESSON_ID1);
        LessonDto lessonDto2 = LessonDto.builder()
            .id(LESSON_ID2)
            .courseId(COURSE_ID1)
            .courseName(NAME_FIRST_COURSE)
            .teacherId(TEACHER_ID1)
            .teacherFullName(FULL_NAME_FIRST_TEACHER)
            .roomId(ROOM_ID1)
            .buildingAndRoom(BUILDING_AND_NUMBER_FIRST_ROOM)
            .timeStart(DATE_START_SECOND_LESSON)
            .timeEnd(DATE_END_SECOND_LESSON)
            .students(new HashSet<>(createTestStudentDtos(ID2)))
            .build();
        return new ArrayList<>(Arrays.asList(lessonDto1, lessonDto2));
    }

    public static LessonFilter createTestLessonFilter() {
        return LessonFilter.builder()
            .facultyId(FACULTY_ID1)
            .departmentId(DEPARTMENT_ID1)
            .teacherId(TEACHER_ID1)
            .courseId(COURSE_ID1)
            .roomId(ROOM_ID1)
            .dateFrom(DATE_FROM)
            .dateTo(DATE_TO)
            .build();
    }
}
