INSERT INTO faculties (id, faculty_name)
VALUES (10, 'IT faculty');
INSERT INTO faculties (id, faculty_name)
VALUES (4, 'Chemistry faculty');


INSERT INTO departments (id, department_name, faculty_id)
VALUES (8, 'Java department', 10);

INSERT INTO departments (id, department_name, faculty_id)
VALUES (54, 'C# department', 10);

INSERT INTO departments (id, department_name, faculty_id)
VALUES (24, 'JavaScript department', 4);

SELECT SETVAL('hibernate_sequence', 100);