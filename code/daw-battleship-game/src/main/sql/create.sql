create table _USER(
    id int primary key,
    username varchar(20) not null,
    passwordValidation varchar(32)
);

create table PLAYER(
    _user int primary key,
    number int,
    foreign key (_user) references _USER(id)
);

create table GAME(
    id int primary key,
    state varchar(32)
);

create table CONFIG(
    game int primary key,
    board_size int,
    n_shots int,
    timeout int,
    foreign key (game) references GAME(id)
);

create table BOARD(
    game int primary key,
    is_player_1 boolean,
    foreign key (game) references GAME(id)
);

create table PANEL(
    game int primary key,
    type varchar(32),
    hit boolean,
    foreign key (game) references GAME(id)
);

create table SHIP_TYPE(
    name varchar(32) primary key
);

create table SHIPS(
    game int,
    type varchar(32),
    horizontal boolean,
    foreign key (game) references GAME(id),
    foreign key (type) references SHIP_TYPE(name)
);

create table SHIP(
    game int primary key,
    type varchar(32),
    horizontal boolean,
    foreign key (game) references GAME(id),
    foreign key (type) references SHIP_TYPE(name)
);

create table COORDINATE(
    row int,
    _column int,
    ship int,
    primary key (row, _column, ship),
    foreign key (ship) references SHIP(game)
);