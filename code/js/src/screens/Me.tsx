import * as React from 'react'
import {useEffect, useState} from 'react'
import {Services} from '../services'
import {Link} from 'react-router-dom'
import style from "./Me.module.css"
import {UserDetail} from "./Commons";
import {Loading} from "./Loading";
import {useCurrentUser} from "./auth/Authn";
import {Logger} from "tslog";

const logger = new Logger({ name: "MeComponent" });

export function Me() {
    const currentUser = useCurrentUser()
    const response = Services.fetchUserHome(currentUser)
    const [joinPrevGameButton, setJoinPrevGameButton] = useState(false)

    useEffect(() => {
        let cancelled = false
        async function setGameButtonIfGameIsOngoing() {
            const gameId = await Services.getCurrentGameId(currentUser)
            if (cancelled) {
                return
            }
            if (typeof gameId === 'number') {
                setJoinPrevGameButton(true)
            }
        }
        setGameButtonIfGameIsOngoing()
        return () => {
            cancelled = true
            logger.debug("Me component unmounted")
        }
    }, [joinPrevGameButton, setJoinPrevGameButton])

    if (typeof response === "string") {
        if (response === "Loading") {
            return (
                <Loading />
            )
        }
        else if (response === "Token at fault, or user home link not found") {
            //redirects to login page
            return (
                <div>
                    <p><h2>Please login before accessing your profile</h2></p>
                    <p><Link id={style.login} to="/sign-in">Login</Link></p>
                </div>
            )
        }
    } else {
        if (joinPrevGameButton) {
            return (
                <div id={style.userProfile}>
                    <div className={style.alignCenter}>
                        <UserDetail user={response}/>
                    </div>
                    <Link to="/game" className={style.link}>Resume Match</Link>
                </div>
            )
        } else {
            return (
                <div className={style.alignCenter}>
                    <UserDetail user={response}/>
                </div>
            )
        }
    }
}