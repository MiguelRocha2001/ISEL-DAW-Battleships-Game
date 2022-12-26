import * as React from 'react'
import {ShowSirenProperties} from '../utils/ShowSirenProperties'
import {Link} from 'react-router-dom'
import {Services} from '../services'
import styles from './Home.module.css'


export function Home() {
    Services.useFetchHome() // Fetch the home resource
    return (
        <div id={styles.mainDiv}>
            <div id={styles.content}>
                <p id={styles.overviewBody}>
                    <h2>The best Battleship Game on the web</h2>
                </p>
                <h1 id={styles.overviewTitle}>(Probably)</h1>
            </div>
        </div>
    )
}