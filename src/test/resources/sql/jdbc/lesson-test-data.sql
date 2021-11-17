insert into faculties (faculty_name) values ('Chemical Technology');

insert into departments (department_name, faculty_id) values ('Chemistry', 1);

insert into teachers (teacher_first_name, teacher_last_name, teacher_patronymic, teacher_active, department_id)
values ('Mike', 'Smith', 'Jr', true, 1);
insert into teachers (teacher_first_name, teacher_last_name, teacher_patronymic, teacher_active, department_id)
values ('Alan', 'Coppola', 'Ford', true, 1);

insert into groups (group_name, group_active, faculty_id) values ('21Chem-1', true, 1);
insert into groups (group_name, group_active, faculty_id) values ('21Chem-2', true, 1);

insert into students (student_first_name, student_last_name, student_patronymic, student_active, group_id)
values ('Dean', 'Thompson', 'Ivanovich', true, 1);
insert into students (student_first_name, student_last_name, student_patronymic, student_active, group_id)
values ('Petr', 'Petrov', 'Petrovich', true, 1);
insert into students (student_first_name, student_last_name, student_patronymic, student_active, group_id)
values ('Ivan', 'Ivanov', 'Petrovich', true, 2);
insert into students (student_first_name, student_last_name, student_patronymic, student_active, group_id)
values ('Tatyana', 'Sergeeva', 'Leonidovna', true, 2);

insert into courses (course_name) values ('Organic Chemistry');

insert into rooms (building, room_number) values ('building-1', '812b');

insert into lessons (teacher_id, course_id, room_id, time_start, time_end)
values (1, 1, 1, '2021-06-12 14:00:00', '2021-06-12 15:30:00');
insert into lessons (teacher_id, course_id, room_id, time_start, time_end)
values (2, 1, 1, '2021-06-10 14:00:00', '2021-06-10 15:30:00');
insert into lessons (teacher_id, course_id, room_id, time_start, time_end)
values (1, 1, 1, '2021-06-13 14:00:00', '2021-06-10 15:30:00');

insert into students_lessons (student_id, lesson_id) values (1, 1);
insert into students_lessons (student_id, lesson_id) values (2, 1);
insert into students_lessons (student_id, lesson_id) values (3, 2);
insert into students_lessons (student_id, lesson_id) values (4, 2);
