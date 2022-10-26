insert into _user (id, username, password_validation) overriding system value
values (0, 'Antonio', 'a'); -- id = 0
values (1, 'Manel', 'b'); -- id = 1
values (2, 'Zezocas', 'c'); -- id = 2
values (3, 'Joao', 'd'); -- id = 3

insert into token(token_validation, user_id)
values ('token1', 0);
values ('token2', 1);
values ('token2', 3);

insert into game(id, state, player1, player2)
values (1, 'fleet_setup', 0, 1);
values (2, 'fleet_setup', 2, 3);

