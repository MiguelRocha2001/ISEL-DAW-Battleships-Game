import * as React from 'react'
import {ShowSirenProperties} from '../utils/ShowSirenProperties'
import {Link} from 'react-router-dom'
import {Services} from '../services'
import styles from './Home.module.css'

export function Home() {
    Services.useFetchHome() // Fetch the home resource
    return (
        <div id={styles.mainDiv}>
            <img src={'https://nationalinterest.org/sites/default/files/main_images/1024px-New_Jersey_Sails.jpg'} id={styles.mainImg} alt="background-image"/>
            <div id={styles.content}>
                <h1 id={styles.overviewTitle}>Overview</h1>
                <p id={styles.overviewBody}>
                    Just create an account and start playing with your friends the newly Battleships game...
                </p>
            </div>
        </div>
    )
}