import * as React from 'react'
import {Rankings, Services, Stats} from '../services'
import styles from './Leaderboard.module.css'

export function Leaderboard() {
    const rankings = Services.fetchBattleshipRanks()
    console.log(rankings)
    if (typeof rankings === 'string') {
        return <p>Loading...</p>
    }
    else {
        return (
            <LeaderboardInternal rankings={rankings}/>
        )
    }
}

function LeaderboardInternal({rankings}: { rankings: Rankings }) {
    return (
        <div>
            <h2>Leadership</h2>
            <table className={styles.table}>
                <thead className={styles.thead}>
                    <tr className={styles.tr}>
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

function Stats({stats}: { stats: Stats }) {
    return (
        <tr className={styles.tr}>
            <td>{stats.username}</td>
            <td>{stats.wins}</td>
            <td>{stats.gamesPlayed}</td>
        </tr>
    )
}