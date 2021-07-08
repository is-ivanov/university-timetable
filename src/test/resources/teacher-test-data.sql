insert into faculties (faculty_name) values ('Chemical Technology');

insert into departments (department_name, faculty_id) values ('Chemistry', 1);
insert into departments (department_name, faculty_id) values ('Oil Technology', 1);

insert into teachers (teacher_first_name, teacher_last_name, teacher_patronymic, teacher_active, department_id)
values ('Mike', 'Smith', 'Jr', true, 1);
insert into teachers (teacher_first_name, teacher_last_name, teacher_patronymic, teacher_active, department_id)
values ('Alan', 'Coppola', 'Ford', false, 2);