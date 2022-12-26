import * as React from 'react'
import {Services, User, UserHome} from '../services'
import {Link} from 'react-router-dom'
import {ShowSirenProperties} from "../utils/ShowSirenProperties";
import style from "./Me.module.css"
import {UserDetail} from "./Commons";

export function Me() {
    const response = Services.fetchUserHome()
    console.log(response)
    if (typeof response === "string") {
        return (
            <p>{response}</p>
        )
    } else {
        return (
            <div>
                <UserDetail user={response}/>
                <Link to="/game" className={style.link}>Game Screen</Link>
            </div>
        )
    }
}