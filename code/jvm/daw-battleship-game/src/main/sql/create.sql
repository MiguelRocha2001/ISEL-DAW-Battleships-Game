create table _USER
(
    id                  int generated always as identity primary key,
    username            VARCHAR(64) unique not null,
    password_validation VARCHAR(256)       not null,
    gamesPlayed        int default 0      not null,
    wins                int default 0      not null
);

create table USER_QUEUE
(
    priority    serial,
    _user       int references _USER (id) primary key,
    quick_game  bool    not null default false,
    config_hash int     not null,
    config      varchar not null
);

create table TOKEN
(
    token_validation VARCHAR(256) primary key,
    user_id          int references _USER (id)
);

create table GAME
(
    id          serial primary key,
    state       varchar(32)     default 'fleet_setup' check (state in ('not_started', 'fleet_setup', 'waiting', 'battle', 'finished')),
    player1     int    not null,
    player2     int    not null,
    winner      int             default null,
    player_turn int             default null,
    created     bigint not null default 0,
    updated     bigint not null default 0,
    deadline    bigint not null default 0,
    foreign key (player1) references _USER (id),
    foreign key (player2) references _USER (id),
    foreign key (player_turn) references _USER (id),
    foreign key (winner) references _USER (id)
);

create table CONFIGURATION
(
    game       int primary key,
    board_size int          not null check (board_size >= 8 and board_size <= 13) default 10,
    n_shots    int          not null                                              default 1,
    timeout    int          not null                                              default 120,
    fleet      varchar(300) not null                                              default (
        '{"CARRIER":5,"BATTLESHIP":4,"CRUISER":3,"SUBMARINE":3,"DESTROYER":2}'
        ),
    foreign key (game) references GAME (id)

);

create table BOARD
(
    game      int,
    _user     int,
    confirmed boolean default false,
    grid      varchar(225) not null,
    primary key (game, _user),
    foreign key (game) references GAME (id),
    foreign key (_user) references _USER (id)
);


