CREATE TABLE public.teachers (
    id serial NOT NULL,
    first_name varchar(100) NOT NULL,
    last_name varchar(100) NOT NULL,
    patronymic varchar(100),
    department_id integer,
    CONSTRAINT PK_teachers PRIMARY KEY (id)
);

CREATE TABLE public.faculties (
    id serial NOT NULL,
    name varchar(255) NOT NULL,
    CONSTRAINT PK_faculties PRIMARY KEY (id)   
);

CREATE TABLE public.groups (
    id serial NOT NULL,
    name varchar(15) NOT NULL,
    faculty_id integer NOT NULL,
    CONSTRAINT PK_groups PRIMARY KEY (id)
);

CREATE TABLE public.departments (
    id serial NOT NULL,
    name varchar(255) NOT NULL,
    faculty_id integer NOT NULL,
    CONSTRAINT PK_departments PRIMARY KEY (id)
);    
  
CREATE TABLE public.students (
    id serial NOT NULL,
    first_name varchar(100) NOT NULL,
    last_name varchar(100) NOT NULL,
    patronymic varchar(100),
    group_id integer NOT NULL,
    CONSTRAINT PK_students PRIMARY KEY (id)
);

CREATE TABLE public.rooms (
    id serial NOT NULL,
    building varchar(50) NOT NULL,
    number varchar(15) NOT NULL,
    CONSTRAINT PK_rooms PRIMARY KEY (id)
);

CREATE TABLE public.courses (
    id serial NOT NULL,
    name varchar(255) NOT NULL,
    CONSTRAINT PK_courses PRIMARY KEY (id)
);

CREATE TABLE public.lessons (
    id serial NOT NULL,
    teacher_id integer NOT NULL,
    course_id integer NOT NULL,
    room_id integer NOT NULL,
    time_start timestamp NOT NULL,
    time_end timestamp NOT NULL,
    CONSTRAINT PK_lessons PRIMARY KEY (id)
);

CREATE TABLE public.students_lessons (
    student_id integer NOT NULL,
    lesson_id integer NOT NULL,
    CONSTRAINT PK_students_lessons PRIMARY KEY (student_id, lesson_id)
);

ALTER TABLE groups ADD CONSTRAINT FK_faculty FOREIGN KEY (faculty_id) REFERENCES public.faculties (id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE departments ADD CONSTRAINT FK_faculty FOREIGN KEY (faculty_id) REFERENCES public.faculties (id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE teachers ADD CONSTRAINT FK_department FOREIGN KEY (department_id) REFERENCES public.departments (id) ON DELETE RESTRICT ON UPDATE RESTRICT;
        
ALTER TABLE students ADD CONSTRAINT FK_group FOREIGN KEY (group_id) REFERENCES public.groups (id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE lessons ADD CONSTRAINT FK_course FOREIGN KEY (course_id) REFERENCES public.courses (id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE lessons ADD CONSTRAINT FK_room FOREIGN KEY (room_id) REFERENCES public.rooms (id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE lessons ADD CONSTRAINT FK_teacher FOREIGN KEY (teacher_id) REFERENCES public.teachers (id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE students_lessons ADD CONSTRAINT FK_students FOREIGN KEY (student_id) REFERENCES public.students (id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE students_lessons ADD CONSTRAINT FK_lesson FOREIGN KEY (lesson_id) REFERENCES public.lessons (id) ON DELETE RESTRICT ON UPDATE RESTRICT;

