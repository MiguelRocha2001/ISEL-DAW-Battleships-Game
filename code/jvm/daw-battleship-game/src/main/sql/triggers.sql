create or replace function userStatsUpdate() returns trigger as $ustats$
BEGIN
    if (TG_OP = 'UPDATE') then
        if NEW.state = 'finished' then
            update _user set wins = wins + 1 where id = NEW.winner;
        end if;
    end if;

    if (TG_OP = 'INSERT') then
        update _user set gamesPlayed = gamesPlayed + 1
        where id = NEW.player1 or id = NEW.player2;
    end if;

    return NEW;
end;
$ustats$ language plpgsql;

create trigger ustats after insert or update on
    game for each row execute function userStatsUpdate();