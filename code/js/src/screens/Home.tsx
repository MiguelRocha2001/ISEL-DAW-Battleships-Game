import * as React from 'react'
import {Services} from '../services'
import styles from './Home.module.css'
import {Logger} from "tslog";

const logger = new Logger({ name: "HomeComponent" });

export function Home() {
    logger.debug("Rendering home page")

    Services.useFetchHome() // Fetch the home resource

    return (
        <div id={styles.mainDiv}>
            <div id={styles.content}>
                <div id={styles.overviewBody}>
                    <h2>The best Battleship Game on the web</h2>
                </div>
                <h1 id={styles.overviewTitle}>(Probably)</h1>
            </div>
        </div>
    )
}