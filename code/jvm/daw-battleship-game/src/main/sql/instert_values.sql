insert into _user (id, username, password_validation) overriding system value values
(0, 'Antonio', 'a'),
(1, 'Manel', 'b'),
(2, 'Zezocas', 'c'),
(3, 'Joao', 'd');
--ignore intellij errors

insert into token(token_validation, user_id) values
('token0', 0),
('token1', 1),
('token2', 2),
('token3', 3);


insert into game(id, state, player1, player2) values
(1, 'fleet_setup', 0, 1),
(2, 'fleet_setup', 2, 3);

insert into configuration(game, board_size, n_shots, timeout) values
(1, 10, 1, 5),
(2, 10, 1, 5);

insert into board(game, _user, confirmed, grid) values
(1, 0, false, '                                                                                                    '),
(1, 1, false, '                                                                                                    '),
(2, 2, false, '                                                                                                    '),
(2, 3, false, '                                                                                                    ');

insert into ship(configuration, name, length) VALUES
(1, 'kruiser', 3),
(2, 'kruiser', 3);
