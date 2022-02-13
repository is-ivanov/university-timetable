INSERT INTO faculties (id, faculty_name)
VALUES (10, 'IT faculty');


INSERT INTO departments (id, department_name, faculty_id)
VALUES (8, 'Java department', 10);

INSERT INTO departments (id, department_name, faculty_id)
VALUES (54, 'C# department', 10);


INSERT INTO teachers (id, first_name, patronymic, last_name,
                      active, department_id)
VALUES (7, 'Ivan', 'Petrovich', 'Ivanov', TRUE, 8);

INSERT INTO teachers (id, first_name, patronymic, last_name,
                      active, department_id)
VALUES (78, 'Oleg', 'Ivanovich', 'Petrov', TRUE, 8);

INSERT INTO teachers (id, first_name, patronymic, last_name,
                      active, department_id)
VALUES (12, 'John', 'Jr', 'Thompson', TRUE, 54);


INSERT INTO courses (id, course_name)
VALUES (45, 'Java');


INSERT INTO rooms (id, building, room_number)
VALUES (5, 'building-1', '1457a');


INSERT INTO lessons (id, teacher_id, course_id, room_id, time_start,
                     time_end)
VALUES (23, 7, 45, 5, '2021-09-12 14:00:00', '2021-09-12 15:30:00');

INSERT INTO lessons (id, teacher_id, course_id, room_id, time_start,
                     time_end)
VALUES (2, 78, 45, 5, '2021-09-12 15:00:00', '2021-09-12 16:30:00');

SELECT SETVAL('hibernate_sequence', 100);