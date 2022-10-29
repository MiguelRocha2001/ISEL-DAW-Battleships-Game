create table _USER(
    id int generated always as identity primary key,
    username VARCHAR(64) unique not null,
    password_validation VARCHAR(256) not null
);

create table USER_QUEUE(
    priority serial,
    _user int references _USER(id) primary key
);

create table TOKEN(
   token_validation VARCHAR(256) primary key,
   user_id int references _USER(id)
);

create table GAME(
    id int primary key,
    state varchar(32) check (state in ('not_started', 'fleet_setup', 'waiting', 'battle' , 'finished')),
    player1 int not null,
    player2 int not null,
    winner int default null,
    player_turn int default null,
    foreign key (player1) references _USER(id),
    foreign key (player2) references _USER(id),
    foreign key (player_turn) references _USER(id),
    foreign key (winner) references _USER(id)
);

create table CONFIGURATION(
    game int primary key,
    board_size int not null,
    n_shots int not null,
    timeout int not null,
    foreign key (game) references GAME(id)
);

create table BOARD(
    game int,
    _user int not null,
    confirmed boolean,
    grid varchar(225) not null,
    primary key (game, _user),
    foreign key (game) references GAME(id),
    foreign key (_user) references _USER(id)
);

create table SHIP(
    configuration int,
    name varchar(20) not null,
    length int,
    primary key (configuration, name),
    foreign key (configuration) references CONFIGURATION(game)
);