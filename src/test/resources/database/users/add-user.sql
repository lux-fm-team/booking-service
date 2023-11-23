INSERT INTO users (id, email, first_name, last_name , password , is_deleted)
VALUES (1, 'test@check.com', 'testFirstName', 'testLastName' , '$2a$10$Qzz79ycwjsShQuaQAF646.p23RnVjfH7pkimLIT7uPldlQkt8vIp2' , false);
INSERT INTO users_roles(user_id, role_id) VALUES (1, 1);
INSERT INTO users_roles(user_id, role_id) VALUES (1, 2);
INSERT INTO users_roles(user_id, role_id) VALUES (1, 3);