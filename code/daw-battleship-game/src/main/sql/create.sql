create table BOARD (
    id int primary key
);

create table SQUARE (
    line int,
    col int,
    hit boolean,
    board_id int,
    primary key (line, col),
    foreign key (board_id) references BOARD(id)
);