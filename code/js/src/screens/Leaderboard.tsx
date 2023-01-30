import * as React from 'react'
import {Fetching, Rankings, Services, UserStats} from '../services'
import styles from './Leaderboard.module.css'
import {Link, useParams} from "react-router-dom";
import {Loading} from "./Loading";
import {ErrorScreen} from "../utils/ErrorScreen";

const PAGE_SIZE = 3

export function Leaderboard() {
    const { page } = useParams()
    const result = Services.useFetchBattleshipRanks(Number(page), PAGE_SIZE)
    const curPage = Number(page)

    if (result instanceof Error) {
        return <ErrorScreen error={result}/>
    } else if (result instanceof Fetching) {
        return <Loading />
    } else {
        const prevButton = curPage > 1 ? <Link to={`/leadership/${curPage - 1}`}>Previous</Link>
            : <span></span>
        const nextButton = result.users.length >= PAGE_SIZE ? <Link to={`/leadership/${curPage + 1}`}>Next</Link>
            : <span></span>

        return (
            <div>
                <LeaderboardInternal rankings={result} />
                {prevButton}
                {nextButton}
            </div>
        )
    }
}

function LeaderboardInternal({rankings}: { rankings: Rankings }) {

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