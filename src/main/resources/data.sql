INSERT INTO users (insert_date_time, insert_user_id, is_deleted, last_update_date_time,
                   last_update_user_id, first_name, last_name, email, username, password, role)
VALUES ('2025-03-06 10:00:00', 1, FALSE, '2025-03-06 12:00:00', 1, 'John', 'Reese', 'johnreese@email.com',
        'johnreese', 'Abc1', 'Admin');


INSERT INTO game_objects (insert_date_time, insert_user_id, is_deleted, last_update_date_time,
                          last_update_user_id, title, text, user_id)
VALUES ('2025-03-06 10:00:00', 1, FALSE, '2025-03-06 12:00:00', 1, 'Game 1',
        'This is the first game object description.', 1);
