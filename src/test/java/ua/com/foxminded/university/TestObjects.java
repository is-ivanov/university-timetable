package ua.com.foxminded.university;

import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.*;

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
    public static final String FIRST_STUDENT_NAME = "Mike";
    public static final String FIRST_STUDENT_PATRONYMIC = "Jr";
    public static final String FIRST_STUDENT_LAST_NAME = "Smith";
    public static final String SECOND_STUDENT_NAME = "Alan";
    public static final String SECOND_STUDENT_PATRONYMIC = "III";
    public static final String SECOND_STUDENT_LAST_NAME = "Johnson";
    public static final String FIRST_TEACHER_NAME = "Ivan";
    public static final String FIRST_TEACHER_PATRONYMIC = "Petrovich";
    public static final String FIRST_TEACHER_LAST_NAME = "Ivanov";
    public static final String SECOND_TEACHER_NAME = "Oleg";
    public static final String SECOND_TEACHER_PATRONYMIC = "Ivanovich";
    public static final String SECOND_TEACHER_LAST_NAME = "Petrov";
    public static final String THIRD_STUDENT_NAME = "Peter";

    public static Faculty createTestFaculty() {
        return new Faculty(ID1, NAME_FIRST_FACULTY);
    }

    public static Faculty createTestFaculty(int facultyId) {
        return new Faculty(facultyId, NAME_FIRST_FACULTY);
    }

    public static Group createTestGroup(int facultyId) {
        return new Group(ID1, NAME_FIRST_GROUP, createTestFaculty(facultyId), true);
    }

    public static Department createTestDepartment(int facultyId){
        return new Department(ID1, NAME_FIRST_DEPARTMENT, createTestFaculty(facultyId));
    }

    public static List<Faculty> createTestFaculties() {
        Faculty faculty1 = new Faculty(ID1, NAME_FIRST_FACULTY);
        Faculty faculty2 = new Faculty(ID2, NAME_SECOND_FACULTY);
        return Arrays.asList(faculty1, faculty2);
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

    public static List<Student> createTestStudents(int facultyId) {
        Group testGroup = createTestGroup(facultyId);
        Student student1 = Student.builder()
            .id(ID1)
            .firstName(FIRST_STUDENT_NAME)
            .patronymic(FIRST_STUDENT_PATRONYMIC)
            .lastName(FIRST_STUDENT_LAST_NAME)
            .group(testGroup)
            .active(true)
            .build();
        Student student2 = Student.builder()
            .id(ID2)
            .firstName(SECOND_STUDENT_NAME)
            .patronymic(SECOND_STUDENT_PATRONYMIC)
            .lastName(SECOND_STUDENT_LAST_NAME)
            .group(testGroup)
            .active(true)
            .build();

        return Arrays.asList(student1, student2);
    }

    public static List<Teacher> createTestTeachers(int facultyId) {
        Department testDepartment = createTestDepartment(facultyId);
        Teacher teacher1 = Teacher.builder()
            .id(ID1)
            .firstName(FIRST_TEACHER_NAME)
            .patronymic(FIRST_TEACHER_PATRONYMIC)
            .lastName(FIRST_TEACHER_LAST_NAME)
            .department(testDepartment)
            .active(true)
            .build();
        Teacher teacher2 = Teacher.builder()
            .id(ID2)
            .firstName(SECOND_TEACHER_NAME)
            .patronymic(SECOND_TEACHER_PATRONYMIC)
            .lastName(SECOND_TEACHER_LAST_NAME)
            .department(testDepartment)
            .active(true)
            .build();

        return Arrays.asList(teacher1, teacher2);
    }

    public static List<TeacherDto> createTestTeacherDtos(int facultyId) {
        Department testDepartment = createTestDepartment(facultyId);
        TeacherDto teacherDto1 = TeacherDto.builder()
            .id(ID1)
            .firstName(FIRST_TEACHER_NAME)
            .patronymic(FIRST_TEACHER_PATRONYMIC)
            .lastName(FIRST_TEACHER_LAST_NAME)
            .departmentId(testDepartment.getId())
            .departmentName(testDepartment.getName())
            .active(true)
            .build();
        TeacherDto teacherDto2 = TeacherDto.builder()
            .id(ID2)
            .firstName(SECOND_TEACHER_NAME)
            .patronymic(SECOND_TEACHER_PATRONYMIC)
            .lastName(SECOND_TEACHER_LAST_NAME)
            .departmentId(testDepartment.getId())
            .departmentName(testDepartment.getName())
            .active(true)
            .build();

        return Arrays.asList(teacherDto1, teacherDto2);
    }

}
