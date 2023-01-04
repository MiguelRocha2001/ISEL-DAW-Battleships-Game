import {UserStats} from "../services";
import style from "./Me.module.css";
import leaderboard from "./Leaderboard.module.css"
import * as React from "react";


export function UserDetail({user}: { user: UserStats }) {
    return (
        <div>
            <h1 id={leaderboard.topPlayers}>PROFILE</h1>
            <table className={leaderboard.table} id={style.me}>
                <tbody>
                    <tr>
                        <td className={style.key}>
                            USER Nº
                        </td>
                        <td className={style.value}>
                            {user.id}
                        </td>
                    </tr>
                    <tr>
                        <td className={style.key}>
                            USERNAME
                        </td>
                        <td className={style.value}>
                            <strong id = {style.username}>{user.username}</strong>
                        </td>
                    </tr>
                    <tr>
                        <td className={style.key}>
                            Games Played
                        </td>
                        <td className={style.value}>
                            {user.gamesPlayed}
                        </td>
                    </tr>
                    <tr>
                        <td className={style.key}>
                            Wins
                        </td>
                        <td className={style.value}>
                            {user.wins}
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

    )
}