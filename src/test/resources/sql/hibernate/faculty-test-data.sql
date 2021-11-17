INSERT INTO faculties (faculty_id, faculty_name)
VALUES (1, 'IT faculty');

INSERT INTO faculties (faculty_id, faculty_name)
VALUES (2, 'Chemistry faculty');

SELECT SETVAL('hibernate_sequence',
              (SELECT MAX(faculty_id) FROM faculties));