INSERT INTO users (insert_date_time, insert_user_id, is_deleted, last_update_date_time,
                   last_update_user_id, first_name, last_name, email, username, password, role, status, email_verified)
VALUES ('2025-03-06 10:00:00', 1, FALSE, '2025-03-06 12:00:00', 1, 'John', 'Reese', 'johnreese@email.com', 'johnreese', 'Abc1', 'ADMIN', 'ACTIVE', TRUE),
       ('2025-03-06 10:00:00', 1, FALSE, '2025-03-06 12:00:00', 1, 'Mike', 'Smith', 'mikesmith@email.com', 'mikesmith', 'Abc1', 'SELLER', 'ACTIVE', TRUE),
       ('2025-03-06 10:00:00', 1, FALSE, '2025-03-06 12:00:00', 1, 'Alice', 'Green', 'alicegreen@email.com', 'alicegreen', 'Abc1', 'SELLER', 'ACTIVE', TRUE),
       ('2025-03-06 10:00:00', 1, FALSE, '2025-03-06 12:00:00', 1, 'Mary', 'Jane', 'maryjane@email.com', 'maryjane', 'Abc1', 'BUYER', 'ACTIVE', TRUE);


INSERT INTO game_objects (insert_date_time, insert_user_id, is_deleted, last_update_date_time,
                          last_update_user_id, title, text, seller_id)
VALUES ('2025-03-06 10:00:00', 1, FALSE, '2025-03-06 12:00:00', 1, 'Fifa', 'fifa game obj', 2),
       ('2025-03-06 10:00:00', 1, FALSE, '2025-03-06 12:00:00', 1, 'CS:GO', 'cs:go game obj', 2),
       ('2025-03-06 10:00:00', 1, FALSE, '2025-03-06 12:00:00', 1, 'Tekken', 'tekken game obj', 3),
       ('2025-03-06 10:00:00', 1, FALSE, '2025-03-06 12:00:00', 1, 'God Of War', 'god of war game obj', 3);
