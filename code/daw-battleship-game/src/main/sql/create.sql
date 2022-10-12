create table _USER(
    id varchar(20) not null primary key,
    username varchar(20) not null,
    hashed_password varchar(32)
);

create table GAME(
    id int primary key,
    user1 varchar(20) not null,
    user2 varchar(20) not null,
    foreign key (user1) references _USER(id),
    foreign key (user2) references _USER(id)
);

create table CONFIGURATION(
    game int primary key,
    board_size int,
    n_shots int,
    timeout int,
    foreign key (game) references GAME(id)
);

create table STATE(
    game int primary key,
    name varchar(20) not null check ( name in ('preparation', 'waiting', 'battle', 'end') ),
    foreign key (game) references GAME(id)
);

create table BATTLE(
    game int primary key,
    player_turn varchar(20) not null,
    foreign key (game) references GAME(id),
    foreign key (player_turn) references _USER(id)
);

create table BOARD(
    game int,
    _user varchar(20) not null,
    primary key (game, _user),
    foreign key (game) references GAME(id),
    foreign key (_user) references _USER(id)
);

create table WATER_PANEL(
    game int,
    _user varchar(20) not null,
    idx int,
    is_hit boolean,
    primary key (game, _user, idx),
    foreign key (game) references GAME(id),
    foreign key (_user) references _USER(id)
);

create table SHIP_PANEL(
    game int,
    _user varchar(20) not null,
    idx int,
    is_hit boolean,
    type varchar(20) not null,
    primary key (game, _user, idx),
    foreign key (game) references GAME(id),
    foreign key (_user) references _USER(id)
);

create table SHIP_TYPE(
    name varchar(20) not null,
    length int,
    configuration int,
    primary key (name, length),
    foreign key (configuration) references CONFIGURATION(game)
);