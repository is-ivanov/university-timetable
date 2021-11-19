INSERT INTO faculties (faculty_name)
VALUES ('Foreign Language');
INSERT INTO faculties (faculty_name)
VALUES ('Chemical Technology');

INSERT INTO groups (group_name, group_active, faculty_id)
VALUES ('20Eng-1', TRUE, 1);
INSERT INTO groups (group_name, group_active, faculty_id)
VALUES ('21Ger-1', TRUE, 1);
INSERT INTO groups (group_name, group_active, faculty_id)
VALUES ('21ORG-3', FALSE, 1);

INSERT INTO departments (department_name, faculty_id)
VALUES ('Chemistry', 1);

INSERT INTO teachers (teacher_first_name, teacher_last_name, teacher_patronymic,
                      department_id, teacher_active)
VALUES ('Mark', 'Aurelia', 'I', 1, TRUE);

INSERT INTO students (student_first_name, student_last_name, student_patronymic,
                      student_active, group_id)
VALUES ('Dean', 'Thompson', 'Ivanovich', TRUE, 1);
INSERT INTO students (student_first_name, student_last_name, student_patronymic,
                      student_active, group_id)
VALUES ('Petr', 'Petrov', 'Petrovich', TRUE, 1);
INSERT INTO students (student_first_name, student_last_name, student_patronymic,
                      student_active, group_id)
VALUES ('Ivan', 'Ivanov', 'Petrovich', TRUE, 2);
INSERT INTO students (student_first_name, student_last_name, student_patronymic,
                      student_active, group_id)
VALUES ('Tatyana', 'Sergeeva', 'Leonidovna', TRUE, 2);


INSERT INTO rooms (building, room_number)
VALUES ('First Corp', '145');

INSERT INTO courses (course_name)
VALUES ('Organic Chemistry');

INSERT INTO lessons (teacher_id, course_id, room_id, time_start, time_end)
VALUES (1, 1, 1, '2021-06-12 14:00:00', '2021-06-12 15:30:00');
INSERT INTO lessons (teacher_id, course_id, room_id, time_start, time_end)
VALUES (1, 1, 1, '2021-06-10 14:00:00', '2021-06-10 15:30:00');

INSERT INTO students_lessons (student_id, lesson_id)
VALUES (1, 1);
INSERT INTO students_lessons (student_id, lesson_id)
VALUES (3, 2);
INSERT INTO students_lessons (student_id, lesson_id)
VALUES (4, 2);