import * as React from 'react'
import {Author, ServerInfo, Services} from '../services'
import styles from './Info.module.css'

export function Info() {
    const resp = Services.useFetchServerInfo()
    if (typeof resp == "string") {
        return (
            <p>{resp}</p>
        )
    }
    else {
        return (
            <ServerInfo info={resp}/>
        )
    }
}

function ServerInfo({info}: { info: ServerInfo }) {
    return (
        <div>
            <Authors authors={info.authors}/>
            <SysVersion version={info.systemVersion}/>
        </div>
    )
}


function Authors({authors}: { authors: Author[] }) {
    return (
        <div>
            <h2>Backend Devs</h2>
            <ul className={styles.list}>
                {authors.map((author) => <Author author={author}/>)}
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

function SysVersion({version}: { version: string }) {
    return (
        <div>
            <h2>Version <span id={styles.version}>{version}</span></h2>
        </div>
    )
}