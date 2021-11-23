INSERT INTO rooms (room_id, building, room_number)
VALUES (1, 'building-1', '1457a');

INSERT INTO rooms (room_id, building, room_number)
VALUES (2, 'building-2', '812b');

INSERT INTO rooms (room_id, building, room_number)
VALUES (3, 'building-2', '543/1');

SELECT SETVAL('hibernate_sequence',
              (SELECT MAX(id) FROM rooms));
