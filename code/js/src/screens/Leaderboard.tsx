import * as React from 'react'
import {Fetching, Rankings, ServerInfo, Services, UserStats} from '../services'
import styles from './Leaderboard.module.css'
import {Link} from "react-router-dom";
import {Loading} from "./Loading";
import {ErrorScreen} from "../utils/ErrorScreen";

export function Leaderboard() {
    const response = Services.fetchBattleshipRanks()

    if (response instanceof Error) {
        return <ErrorScreen param={response.message}/>
    } else if (response instanceof Fetching) {
        return <Loading />
    } else {
        return <LeaderboardInternal rankings={response}/>
    }
}


function LeaderboardInternal({rankings}: { rankings: Rankings }) {
    console.log(rankings.users)
    if (rankings.users.length === 0) {
        return <div><h2>No player found <em>(yet)</em></h2></div>
    } else return (
        <div id ={styles.leaderBoard}>
            <h1 id={styles.topPlayers}>TOP {rankings.users.length} PLAYERS</h1>
            <table className={styles.table}>
                <thead>
                    <tr>
                        <th className={styles.centered} >Username</th>
                        <th className={styles.centered} >Wins</th>
                        <th className={styles.centered} >Games Played</th>
                    </tr>
                </thead>
                <tbody>
                    {rankings.users.map((stats) => <Stats key={stats.id} stats={stats}/>)}
                </tbody>
            </table>
        </div>
    )
}

function Stats({stats}: { stats: UserStats }) {
    const userLink = `/users/${stats.id}`
    return (
        <tr>
            <td className={styles.td}>
                <Link id={styles.username} to={userLink}>{stats.username}</Link>
            </td>
            <td className={styles.td}>{stats.wins}</td>
            <td className={styles.td}>{stats.gamesPlayed}</td>
        </tr>
    )
}