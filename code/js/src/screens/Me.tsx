import * as React from 'react'
import {useEffect, useState} from 'react'
import {Fetching, Services} from '../services'
import {Link} from 'react-router-dom'
import style from "./Me.module.css"
import {UserDetail} from "./Commons";
import {Loading} from "./Loading";
import {ErrorScreen} from "./ErrorScreen";

export function Me() {
    const result = Services.fetchUserHome()
    const [joinPrevGameButton, setJoinPrevGameButton] = useState(false)

    useEffect(() => {
        async function setGameButtonIfGameIsOngoing() {
            const gameId = await Services.getCurrentGameId()
            if (typeof gameId === 'number') {
                setJoinPrevGameButton(true)
            }
        }
        setGameButtonIfGameIsOngoing()
    }, [joinPrevGameButton, setJoinPrevGameButton])

    if (result instanceof Error) {
        if (result.message === 'User home link not found') {
            return (
                <div>
                    <h2>Please login before accessing your profile</h2>
                    <p><Link id={style.login} to="/sign-in">Login</Link></p>
                </div>
            )
        } else {
                return <ErrorScreen error={result}/>
        }
    } else if (result instanceof Fetching) {
        return <Loading />
    } else {
        if (joinPrevGameButton) {
            return (
                <div id={style.userProfile}>
                    <div className={style.alignCenter}>
                        <UserDetail user={result}/>
                    </div>
                    <Link to="/game" className={style.link}>Resume Match</Link>
                </div>
            )
        } else {
            return (
                <div className={style.alignCenter}>
                    <UserDetail user={result}/>
                </div>
            )
        }
    }
}