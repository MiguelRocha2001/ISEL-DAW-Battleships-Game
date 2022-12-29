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
    if(rankings.users.length === 0) {
        return <div><h2>No one is here <em>(yet)</em></h2></div>
    }else return (
        <div id ={styles.leaderBoard}>
            <h2 id={styles.topPlayers}>TOP {rankings.users.length} PLAYERS</h2>
            <table className={styles.table}>
                <thead>
                    <tr>
                        <th>Username</th>
                        <th>Wins</th>
                        <th>Games Played</th>
                    </tr>
                </thead>
                <tbody>
                {rankings.users.map((stats) => <Stats stats={stats}/>)}
                </tbody>
            </table>
        </div>
    )
}

function Stats({stats}: { stats: UserStats }) {
    const userLink = `/users/${stats.id}`
    return (
        <tr key={stats.id}>
            <td>
                <Link to={userLink}>{stats.username}</Link>
            </td>
            <td>{stats.wins}</td>
            <td>{stats.gamesPlayed}</td>
        </tr>
    )
}