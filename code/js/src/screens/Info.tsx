import * as React from 'react'
import {Author, ServerInfo, Services} from '../services'
import styles from './Info.module.css'
import {Loading} from "./Loading";
import {Error} from "../utils/Error";

export function Info() {
    const response = Services.useFetchServerInfo()
    if (typeof response === "string") {
        if (response === "Loading") {
            return <Loading />
        }
        else {
            return <Error param={response}/>
        }
    }
    else {
        return (
            <ServerInfo info={response}/>
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