INSERT INTO courses (course_id, course_name)
VALUES (1, 'Java');

INSERT INTO courses (course_id, course_name)
VALUES (2, 'English');

INSERT INTO courses (course_id, course_name)
VALUES (3, 'Chemistry');

SELECT SETVAL('hibernate_sequence',
              (SELECT MAX(course_id) FROM courses));