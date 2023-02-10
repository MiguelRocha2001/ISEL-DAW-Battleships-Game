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
    const [showJoinPrevGameButton, setShowJoinPrevGameButton] = useState(false)
    const [showAbandonGameQueueButton, setAbandonGameQueueButton] = useState(false)

    useEffect(() => {
        async function setGameButtonIfGameIsOngoing() {
            const gameId = await Services.getCurrentGameId()
            if (typeof gameId === 'number') {
                setShowJoinPrevGameButton(true)
            }
        }
        async function setAbandonGameQueueButtonIfGameIsQueued() {
            const inGameQueue = await Services.isInGameQueue()
            console.log('inGameQueue: ', inGameQueue)
            if (typeof inGameQueue === 'boolean' && inGameQueue) {
                setAbandonGameQueueButton(true)
            }
        }

        setGameButtonIfGameIsOngoing()
        setAbandonGameQueueButtonIfGameIsQueued()
    }, [showJoinPrevGameButton, setShowJoinPrevGameButton])

    async function abandonGameQueue() {
        await Services.quitGameQueue()
        setAbandonGameQueueButton(false)
    }

    let joinGameButton = <span />
    let abandonGameQueueButton = <span />
    if (showJoinPrevGameButton)
        joinGameButton = <Link to="/game" className={style.link}>Resume Match</Link>
    if (showAbandonGameQueueButton)
        abandonGameQueueButton = <button onClick={abandonGameQueue} className={style.link}>Abandon Game Queue</button>

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
        return (
            <div id={style.userProfile}>
                <div className={style.alignCenter}>
                    <UserDetail user={result}/>
                </div>
                {joinGameButton}
                {abandonGameQueueButton}
            </div>
        )
    }
}