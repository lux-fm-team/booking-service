INSERT INTO users (id, email, first_name, last_name , password , is_deleted)
VALUES (1, 'test@check.com', 'testFirstName', 'testLastName' , '$2a$10$Qzz79ycwjsShQuaQAF646.p23RnVjfH7pkimLIT7uPldlQkt8vIp2' , false);

INSERT INTO accommodations(id , location,type,size,daily_rate,availability,is_deleted)
values (1,'test', 'HOUSE',1,10,3,false);

INSERT INTO bookings (id,user_id,accommodation_id,STATUS, check_in,check_out,time_to_live)
VALUES(1,1,1,'PENDING','2023-11-23' , '2023-11-25','23:59:59');
