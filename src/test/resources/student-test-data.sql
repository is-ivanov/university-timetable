insert into faculties (name) values ('Foreign Language');
insert into faculties (name) values ('Chemical Technology');

insert into groups (name, faculty_id) values ('20Eng-1', 1);
insert into groups (name, faculty_id) values ('21Ger-1', 2);

insert into students (first_name, last_name, patronymic, group_id)
values ('Mike', 'Smith', 'Jr', 1);
insert into students (first_name, last_name, patronymic, group_id)
values ('Alan', 'Coppola', 'Ford', 1);
insert into students (first_name, last_name, patronymic, group_id)
values ('Peter', 'Daddy', 'Dre', 2);

insert into departments (name, faculty_id) values ('Chemistry', 1);

insert into teachers (first_name, last_name, patronymic, department_id)
values ('Nick', 'Bean', 'II', 1);

insert into courses (name) values ('Chemistry');

insert into rooms (building, number) values ('building-1', '812b');

insert into lessons (teacher_id, course_id, room_id, time_start, time_end)
values (1, 1, 1, '2021-06-12 14:00:00', '2021-06-12 15:30:00');
insert into lessons (teacher_id, course_id, room_id, time_start, time_end)
values (1, 1, 1, '2021-06-10 14:00:00', '2021-06-10 15:30:00');


insert into students_lessons (student_id, lesson_id) values (3, 1);
insert into students_lessons (student_id, lesson_id) values (2, 2);