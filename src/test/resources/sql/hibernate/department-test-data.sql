INSERT INTO faculties (faculty_id, faculty_name)
VALUES (1, 'IT faculty');

INSERT INTO departments (department_id, department_name, faculty_id)
VALUES (1, 'Java department', 1);

INSERT INTO departments (department_id, department_name, faculty_id)
VALUES (2, 'C# department', 1);

SELECT SETVAL('hibernate_sequence',
              (SELECT MAX(department_id) FROM departments));