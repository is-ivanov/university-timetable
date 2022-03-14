package ua.com.foxminded.university.ui.util;

public final class MappingConstants {

    public static final String API = "/api";
    public static final String COURSES = "/courses";
    public static final String FACULTIES = "/faculties";
    public static final String GROUPS = "/groups";
    public static final String GROUP_ID = "/{groupId}";
    public static final String LESSONS = "/lessons";
    public static final String ROOMS = "/rooms";
    public static final String DEPARTMENTS = "/departments";
    public static final String STUDENTS = "/students";
    public static final String STUDENT_ID = "/{studentId}";
    public static final String TEACHERS = "/teachers";
    public static final String FREE = "/free";
    public static final String ID = "/{id}";
    public static final String TIMETABLE = "/timetable";
    public static final String FILTER = "/filter";
    public static final String API_COURSES = API + COURSES;
    public static final String API_COURSES_ID = API + COURSES + ID;
    public static final String API_DEPARTMENTS = API + DEPARTMENTS;
    public static final String API_DEPARTMENTS_ID = API + DEPARTMENTS + ID;
    public static final String API_FACULTIES = API + FACULTIES;
    public static final String API_FACULTIES_ID = API + FACULTIES + ID;
    public static final String API_GROUPS = API + GROUPS;
    public static final String API_GROUPS_ID = API + GROUPS + ID;
    public static final String API_GROUPS_ID_STUDENTS = API_GROUPS_ID + STUDENTS;
    public static final String API_GROUPS_ID_STUDENTS_FREE = API_GROUPS_ID_STUDENTS + FREE;
    public static final String API_LESSONS = API + LESSONS;
    public static final String API_LESSONS_ID = API + LESSONS + ID;
    public static final String API_ROOMS = API + ROOMS;
    public static final String API_ROOMS_ID = API + ROOMS + ID;
    public static final String API_ROOMS_ID_TIMETABLE = API_ROOMS_ID + TIMETABLE;
    public static final String API_ROOMS_FREE = API + ROOMS + FREE;
    public static final String API_STUDENTS = API + STUDENTS;
    public static final String API_STUDENTS_ID = API + STUDENTS + ID;
    public static final String API_TEACHERS = API + TEACHERS;
    public static final String API_TEACHERS_ID = API + TEACHERS + ID;
    public static final String ID_GROUPS = ID + GROUPS;
    public static final String ID_GROUPS_GROUP_ID = ID + GROUPS + GROUP_ID;
    public static final String ID_DEPARTMENTS = ID + DEPARTMENTS;
    public static final String ID_TEACHERS = ID + TEACHERS;
    public static final String ID_GROUPS_FREE = ID + GROUPS + FREE;
    public static final String ID_STUDENTS = ID + STUDENTS;
    public static final String ID_STUDENTS_FREE = ID + STUDENTS + FREE;
    public static final String ID_STUDENTS_STUDENT_ID = ID + STUDENTS + STUDENT_ID;
    public static final String ID_TIMETABLE = ID + TIMETABLE;


    private MappingConstants() {
    }
}
