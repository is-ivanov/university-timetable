insert into teachers (first_name, patronymic, last_name)
values ('Ivan', 'Sergeevich', 'Petrov');

insert into faculties (name, dean_id) values ('Foreign Language', 1);
insert into faculties (name) values ('Chemical Technology');

insert into groups (name, faculty_id) values ('20Eng-1', 1);
insert into groups (name, faculty_id) values ('21Ger-1', 1);

insert into students (first_name, last_name, patronymic, group_id)
values ('Mike', 'Smith', 'Jr', 1);
insert into students (first_name, last_name, patronymic, group_id)
values ('Alan', 'Coppola', 'Ford', 1);