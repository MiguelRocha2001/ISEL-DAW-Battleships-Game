import * as React from 'react'
import {Author, Fetching, ServerInfo, Services} from '../services'
import styles from './Info.module.css'
import {Loading} from "./Loading";
import {ErrorScreen} from "../utils/ErrorScreen";

export function Info() {
    const result = Services.useFetchServerInfo()

    if (result instanceof Error) {
        return <ErrorScreen error={result}/>
    } else if (result instanceof Fetching) {
        return <Loading />
    } else {
        return <ServerInfo info={result}/>
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
            <h2>The Developers</h2>
            <ul className={styles.list}>
                {authors.map((author) => <Author author={author}/>)}
            </ul>
        </div>
    )
}

function Author({author}: { author: Author }) {
    return (
        <li key={author.email} className={styles.element}>
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