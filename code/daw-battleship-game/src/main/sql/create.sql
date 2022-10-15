create table _USER(
    id int generated always as identity primary key,
    username VARCHAR(64) unique not null,
    password_validation VARCHAR(256) not null
);

create table TOKEN(
   token_validation VARCHAR(256) primary key,
   user_id int references _USER(id)
);

create table GAME(
    id int primary key,
    user1 int not null,
    user2 int not null,
    finished boolean,
    player_turn int null,
    foreign key (user1) references _USER(id),
    foreign key (user2) references _USER(id),
    foreign key (player_turn) references _USER(id)
);

create table CONFIGURATION(
    game int primary key,
    board_size int,
    n_shots int,
    timeout int,
    foreign key (game) references GAME(id)
);

create table BOARD(
    game int,
    _user int not null,
    confirmed boolean,
    primary key (game, _user),
    foreign key (game) references GAME(id),
    foreign key (_user) references _USER(id)
);

create table PANEL(
    game int,
    _user int not null,
    x int not null,
    y int not null,
    is_hit boolean not null,
    type varchar(20) not null check ( type in ('water', 'carrier', 'battleship', 'cruiser', 'submarine', 'destroyer') ),
    primary key (game, _user, x, y),
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