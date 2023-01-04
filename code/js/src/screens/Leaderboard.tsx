import * as React from 'react'
import {Rankings, Services, UserStats} from '../services'
import styles from './Leaderboard.module.css'
import {Link} from "react-router-dom";
import {Loading} from "./Loading";

export function Leaderboard() {
    const response = Services.fetchBattleshipRanks()
    if (typeof response === "string") {
        if (response === "Loading") {
            return <Loading />
        }
        else {
            return <h1>{response}</h1>
        }
    }
    else {
        return (
            <LeaderboardInternal rankings={response}/>
        )
    }
}

function LeaderboardInternal({rankings}: { rankings: Rankings }) {
    if (rankings.users.length === 0) {
        return <div><h2>No one is here <em>(yet)</em></h2></div>
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