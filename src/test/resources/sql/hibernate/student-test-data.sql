INSERT INTO faculties (faculty_id, faculty_name)
VALUES (10, 'IT faculty');

INSERT INTO faculties (faculty_id, faculty_name)
VALUES (4, 'Chemistry faculty');


INSERT INTO groups (group_id, group_name, group_active, faculty_id)
VALUES (2, '99XT-1', TRUE, 10);

INSERT INTO groups (group_id, group_name, group_active, faculty_id)
VALUES (14, '56FDS', TRUE, 4);


INSERT INTO students (id, first_name, last_name,
                      patronymic,
                      active, group_id)
VALUES (12, 'Mike', 'Smith', 'Jr', TRUE, 2);

INSERT INTO students (id, first_name, last_name,
                      patronymic,
                      active, group_id)
VALUES (78, 'Alan', 'Johnson', 'III', TRUE, 2);

INSERT INTO students (id, first_name, last_name,
                      patronymic,
                      active, group_id)
VALUES (3, 'Peter', 'Daddy', 'Dre', FALSE, 14);


INSERT INTO departments (department_id, department_name, faculty_id)
VALUES (8, 'Java department', 10);


INSERT INTO teachers (id, first_name, last_name,
                      patronymic,
                      active, department_id)
VALUES (7, 'Ivan', 'Ivanov', 'Petrovich', TRUE, 8);


INSERT INTO courses (course_id, course_name)
VALUES (45, 'Java');


INSERT INTO rooms (room_id, building, room_number)
VALUES (5, 'building-1', '1457a');


INSERT INTO lessons (lesson_id, teacher_id, course_id, room_id, time_start,
                     time_end)
VALUES (23, 7, 45, 5, '2021-06-12 14:00:00', '2021-06-12 15:30:00');

INSERT INTO lessons (lesson_id, teacher_id, course_id, room_id, time_start,
                     time_end)
VALUES (2, 7, 45, 5, '2021-06-10 14:00:00', '2021-06-10 15:30:00');


INSERT INTO students_lessons (student_id, lesson_id)
VALUES (3, 23);

INSERT INTO students_lessons (student_id, lesson_id)
VALUES (78, 2);

SELECT SETVAL('hibernate_sequence', 100);