insert into faculties (faculty_name) values ('Chemical Technology');

insert into departments (department_name, faculty_id) values ('Chemistry', 1);
insert into departments (department_name, faculty_id) values ('Oil Technology', 1);

insert into teachers (teacher_first_name, teacher_last_name, teacher_patronymic, teacher_active, department_id)
values ('Mike', 'Smith', 'Jr', true, 1);
insert into teachers (teacher_first_name, teacher_last_name, teacher_patronymic, teacher_active, department_id)
values ('Alan', 'Coppola', 'Ford', false, 2);
insert into teachers (teacher_first_name, teacher_last_name, teacher_patronymic, teacher_active, department_id)
values ('Ivan', 'Ivanov', 'Sergeevich', TRUE, 1);

insert into courses (course_name) values ('Organic Chemistry');

insert into rooms (building, room_number) values ('building-1', '812b');

insert into lessons (teacher_id, course_id, room_id, time_start, time_end)
values (1, 1, 1, '2021-09-12 14:00:00', '2021-09-12 15:30:00');
insert into lessons (teacher_id, course_id, room_id, time_start, time_end)
values (2, 1, 1, '2021-09-12 15:00:00', '2021-09-12 16:30:00');