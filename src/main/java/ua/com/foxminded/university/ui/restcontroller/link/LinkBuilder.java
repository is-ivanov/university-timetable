package ua.com.foxminded.university.ui.restcontroller.link;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import ua.com.foxminded.university.ui.restcontroller.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public final class LinkBuilder {

    public static final Link ROOT_LINK = linkTo(RootRestController.class).withRel("root");

    public static final WebMvcLinkBuilder COURSE_LINK_BUILDER = linkTo(CourseRestController.class);
    public static final Link COURSES_LINK = COURSE_LINK_BUILDER.withRel("courses");
    public static final Link COURSES_SELF_LINK = COURSE_LINK_BUILDER.withSelfRel();

    public static final WebMvcLinkBuilder FACULTY_LINK_BUILDER = linkTo(FacultyRestController.class);
    public static final Link FACULTIES_LINK = FACULTY_LINK_BUILDER.withRel("faculties");
    public static final Link FACULTIES_SELF_LINK = FACULTY_LINK_BUILDER.withSelfRel();

    public static final WebMvcLinkBuilder GROUP_LINK_BUILDER = linkTo(GroupRestController.class);
    public static final Link GROUPS_LINK = GROUP_LINK_BUILDER.withRel("groups");
    public static final Link GROUPS_SELF_LINK = GROUP_LINK_BUILDER.withSelfRel();

    public static final WebMvcLinkBuilder ROOM_LINK_BUILDER = linkTo(RoomRestController.class);
    public static final Link ROOMS_LINK = ROOM_LINK_BUILDER.withRel("rooms");
    public static final Link ROOMS_SELF_LINK = ROOM_LINK_BUILDER.withSelfRel();

    public static final WebMvcLinkBuilder DEPARTMENT_LINK_BUILDER = linkTo(DepartmentRestController.class);
    public static final Link DEPARTMENTS_LINK = DEPARTMENT_LINK_BUILDER.withRel("departments");
    public static final Link DEPARTMENTS_SELF_LINK = DEPARTMENT_LINK_BUILDER.withSelfRel();

    public static final WebMvcLinkBuilder TEACHER_LINK_BUILDER = linkTo(TeacherRestController.class);
    public static final Link TEACHERS_LINK = TEACHER_LINK_BUILDER.withRel("teachers");
    public static final Link TEACHERS_SELF_LINK = TEACHER_LINK_BUILDER.withSelfRel();

    public static final WebMvcLinkBuilder STUDENT_LINK_BUILDER = linkTo(StudentRestController.class);
    public static final Link STUDENTS_LINK = STUDENT_LINK_BUILDER.withRel("students");
    public static final Link STUDENTS_SELF_LINK = STUDENT_LINK_BUILDER.withSelfRel();

    public static final WebMvcLinkBuilder LESSON_LINK_BUILDER = linkTo(LessonRestController.class);
    public static final Link LESSONS_LINK = LESSON_LINK_BUILDER.withRel("lessons");
    public static final Link LESSONS_SELF_LINK = LESSON_LINK_BUILDER.withSelfRel();

    private LinkBuilder() {
    }
}
