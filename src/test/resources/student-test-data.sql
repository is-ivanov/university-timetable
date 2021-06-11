insert into faculties (name) values ('Foreign Language');
insert into faculties (name) values ('Chemical Technology');

insert into groups (name, faculty_id) values ('20Eng-1', 1);
insert into groups (name, faculty_id) values ('21Ger-1', 2);

insert into students (first_name, last_name, patronymic, group_id)
values ('Mike', 'Smith', 'Jr', 1);
insert into students (first_name, last_name, patronymic, group_id)
values ('Alan', 'Coppola', 'Ford', 1);