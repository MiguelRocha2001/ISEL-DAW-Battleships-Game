import * as React from 'react'
import {Services, UserHome} from '../services'
import {Link} from 'react-router-dom'
import {ShowSirenProperties} from "../utils/ShowSirenProperties";
import style from "./Me.module.css"
import {UserDetail} from "./Commons";
import {Loading} from "./Loading";
import {useEffect, useState} from "react";

export function Me() {
    const [joinPrevGameButton, setJoinPrevGameButton] = useState(false)
    const response = Services.fetchUserHome()

    useEffect(() => {
        async function setGameButtonIfGameIsOngoing() {
            const gameId = await Services.getCurrentGameId()
            if (typeof gameId === 'number') {
                setJoinPrevGameButton(true)
            }
        }
        setGameButtonIfGameIsOngoing()
    }, [joinPrevGameButton, setJoinPrevGameButton])

    if (typeof response === "string") {
        if (response === "Loading") {
            return <Loading />
        }
        else if (response === "Token at fault, or user home link not found") {
            //redirects to login page
            return (
                <div>
                    <p><h2>Please Login before accessing your profile</h2></p>
                    <p><Link id={style.login} to="/sign-in">Login</Link></p>
                </div>
            )
        }
    } else {
        if (joinPrevGameButton) {
            return (
                <div>
                    <UserDetail user={response}/>
                    <Link to="/game" className={style.link}>Game Screen</Link>
                </div>
            )
        } else {
            return (
                <div>
                    <UserDetail user={response}/>
                </div>
            )
        }
    }
}