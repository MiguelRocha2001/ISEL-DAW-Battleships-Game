import {UserStats} from "../services";
import style from "./Me.module.css";
import leaderboard from "./Leaderboard.module.css"
import * as React from "react";

export function UserDetail({user}: { user: UserStats }) {
    return (
        <table className={leaderboard.table}>
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
    )
}