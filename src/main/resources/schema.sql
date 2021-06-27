DROP TABLE IF EXISTS teachers CASCADE;
DROP TABLE IF EXISTS faculties CASCADE;
DROP TABLE IF EXISTS groups CASCADE;
DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS rooms CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS lessons CASCADE;
DROP TABLE IF EXISTS students_lessons CASCADE;

CREATE TABLE public.teachers (
    teacher_id serial NOT NULL,
    teacher_first_name varchar(100) NOT NULL,
    teacher_last_name varchar(100) NOT NULL,
    teacher_patronymic varchar(100),
    teacher_active boolean NOT NULL,
    department_id integer,
    CONSTRAINT PK_teachers PRIMARY KEY (teacher_id)
);

CREATE TABLE public.faculties (
    faculty_id serial NOT NULL,
    faculty_name varchar(255) NOT NULL,
    CONSTRAINT PK_faculties PRIMARY KEY (faculty_id)   
);

CREATE TABLE public.groups (
    group_id serial NOT NULL,
    group_name varchar(15) NOT NULL,
    faculty_id integer NOT NULL,
    group_active boolean NOT NULL,
    CONSTRAINT PK_groups PRIMARY KEY (group_id)
);

CREATE TABLE public.departments (
    department_id serial NOT NULL,
    department_name varchar(255) NOT NULL,
    faculty_id integer NOT NULL,
    CONSTRAINT PK_departments PRIMARY KEY (department_id)
);    
  
CREATE TABLE public.students (
    student_id serial NOT NULL,
    student_first_name varchar(100) NOT NULL,
    student_last_name varchar(100) NOT NULL,
    student_patronymic varchar(100),
    student_active boolean NOT NULL,
    group_id integer NOT NULL,
    CONSTRAINT PK_students PRIMARY KEY (student_id)
);

CREATE TABLE public.rooms (
    room_id serial NOT NULL,
    building varchar(50) NOT NULL,
    room_number varchar(15) NOT NULL,
    CONSTRAINT PK_rooms PRIMARY KEY (room_id)
);

CREATE TABLE public.courses (
    course_id serial NOT NULL,
    course_name varchar(255) NOT NULL,
    CONSTRAINT PK_courses PRIMARY KEY (course_id)
);

CREATE TABLE public.lessons (
    lesson_id serial NOT NULL,
    teacher_id integer NOT NULL,
    course_id integer NOT NULL,
    room_id integer NOT NULL,
    time_start timestamp NOT NULL,
    time_end timestamp NOT NULL,
    CONSTRAINT PK_lessons PRIMARY KEY (lesson_id)
);

CREATE TABLE public.students_lessons (
    student_id integer NOT NULL,
    lesson_id integer NOT NULL,
    CONSTRAINT PK_students_lessons PRIMARY KEY (student_id, lesson_id)
);

ALTER TABLE groups ADD CONSTRAINT FK_groups_faculty FOREIGN KEY (faculty_id) REFERENCES public.faculties (faculty_id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE departments ADD CONSTRAINT FK_departments_faculty FOREIGN KEY (faculty_id) REFERENCES public.faculties (faculty_id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE teachers ADD CONSTRAINT FK_teachers_department FOREIGN KEY (department_id) REFERENCES public.departments (department_id) ON DELETE RESTRICT ON UPDATE RESTRICT;
        
ALTER TABLE students ADD CONSTRAINT FK_students_group FOREIGN KEY (group_id) REFERENCES public.groups (group_id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE lessons ADD CONSTRAINT FK_lessons_course FOREIGN KEY (course_id) REFERENCES public.courses (course_id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE lessons ADD CONSTRAINT FK_lessons_room FOREIGN KEY (room_id) REFERENCES public.rooms (room_id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE lessons ADD CONSTRAINT FK_lessons_teacher FOREIGN KEY (teacher_id) REFERENCES public.teachers (teacher_id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE students_lessons ADD CONSTRAINT FK_students_lessons_students FOREIGN KEY (student_id) REFERENCES public.students (student_id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE students_lessons ADD CONSTRAINT FK_students_lessons_lesson FOREIGN KEY (lesson_id) REFERENCES public.lessons (lesson_id) ON DELETE RESTRICT ON UPDATE RESTRICT;
