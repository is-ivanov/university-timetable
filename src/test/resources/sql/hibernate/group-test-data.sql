INSERT INTO faculties (id, faculty_name)
VALUES (1, 'IT faculty');
INSERT INTO faculties (id, faculty_name)
VALUES (2, 'Chemistry faculty');

INSERT INTO groups (id, group_name, group_active, faculty_id)
VALUES (1, '99XT-1', TRUE, 1);
INSERT INTO groups (id, group_name, group_active, faculty_id)
VALUES (2, '56FDS', TRUE, 1);
INSERT INTO groups (id, group_name, group_active, faculty_id)
VALUES (3, '21Ger-1', FALSE, 1);

INSERT INTO departments (id, department_name, faculty_id)
VALUES (1, 'Java department', 1);

INSERT INTO teachers (id, first_name, patronymic, last_name, department_id, active)
VALUES (1, 'Ivan', 'Petrovich', 'Ivanov', 1, TRUE);

INSERT INTO students (id, first_name, patronymic, last_name, active, group_id)
VALUES (1, 'Mike', 'Jr', 'Smith', TRUE, 1);
INSERT INTO students (id, first_name, patronymic, last_name, active, group_id)
VALUES (2, 'Alan', 'III', 'Johnson', TRUE, 1);
INSERT INTO students (id, first_name, patronymic, last_name, active, group_id)
VALUES (3, 'Alex', 'Petrovich', 'Grade', TRUE, 2);
INSERT INTO students (id, first_name, patronymic, last_name, active, group_id)
VALUES (4, 'Tatyana', 'Leonidovna', 'Sergeeva', TRUE, 2);

INSERT INTO rooms (id, building, room_number)
VALUES (1, 'building-1', '1457a');

INSERT INTO courses (id, course_name)
VALUES (1, 'Java');

INSERT INTO lessons (id, teacher_id, course_id, room_id, time_start, time_end)
VALUES (1, 1, 1, 1, '2021-06-12 14:00:00', '2021-06-12 15:30:00');
INSERT INTO lessons (id, teacher_id, course_id, room_id, time_start, time_end)
VALUES (2, 1, 1, 1, '2021-06-10 14:00:00', '2021-06-10 15:30:00');

INSERT INTO students_lessons (student_id, lesson_id)
VALUES (1, 1);
INSERT INTO students_lessons (student_id, lesson_id)
VALUES (3, 2);
INSERT INTO students_lessons (student_id, lesson_id)
VALUES (4, 2);

SELECT SETVAL('hibernate_sequence',
              (SELECT MAX(id) FROM students));