import * as React from 'react'
import {ShowSirenProperties} from '../utils/ShowSirenProperties'
import {Author, ServerInfo, Services} from '../services'
import styles from './Info.module.css'

export function Info() {
    const content = Services.useFetchServerInfo()
    console.log('content: ', content)
    if (typeof content == "string") {
        return (
            <div>Loading...</div>
        )
    }
    else {
        return (
            <ServerInfo info={content}/>
        )
    }
}

function ServerInfo({info}: { info: ServerInfo }) {
    return (
        <div>
            <h2>Backend Devs</h2>
            <ul className={styles.list}>
                {info.authors.map((author) => <Author author={author}/>)}
            </ul>
        </div>
    )
}


function Author({author}: { author: Author }) {
    return (
        <li className={styles.element}>
            <text className={styles.name}>{author.name}</text>
            <br/><br/>
            <text className={styles.email}>{author.email}</text>
        </li>
    )
}
