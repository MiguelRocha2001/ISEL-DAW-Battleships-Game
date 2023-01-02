import {UserStats} from "../services";
import style from "./Me.module.css";
import leaderboard from "./Leaderboard.module.css"
import * as React from "react";
import styles from "./Leaderboard.module.css";

export function UserDetail({user}: { user: UserStats }) {
    return (
        <div>
            <h1 id={leaderboard.topPlayers}>PROFILE</h1>
            <table className={leaderboard.table} id={style.me}>
                <tr>
                    <td className={style.key}>
                        USER Nº
                    </td>
                    <span className={style.spacer} />
                    <td className={style.value}>
                        {user.id}
                    </td>
                </tr>
                <tr>
                    <td className={style.key}>
                        USERNAME
                    </td>
                    <span className={style.spacer} />
                    <td  className={style.value}>
                        <strong id = {style.username}>{user.username}</strong>
                    </td>
                </tr>
                <tr>
                    <td className={style.key}>
                        Games Played
                    </td>
                    <span className={style.spacer} />
                    <td className={style.value}>
                        {user.gamesPlayed}
                    </td>
                </tr>
                <tr>
                    <td className={style.key}>
                        Wins
                    </td>
                    <span className={style.spacer} />
                    <td className={style.value}>
                        {user.wins}
                    </td>
                </tr>
            </table>
        </div>

    )
}