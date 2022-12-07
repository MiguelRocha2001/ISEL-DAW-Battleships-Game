import * as React from 'react'
import {
    useReducer,
    useEffect,
} from 'react'
import { Services } from '../services'
import { CreateGameRequest } from '../domain'
import { Logger } from "tslog";


const logger = new Logger({ name: "GameScreen" });

type State = 
    {
        type : "starting",
    }
    |
    {
        type : "waiting"
    }
    |
    {
        type : "waitingWithMsg",
        msg : string
    }
    |
    {
        type : "playing",
        gameId : number
    }

type Action =     
    {
        type : "setWaiting"
    }
    |
    {
        type : "setPlaying",
        gameId : number
    }
    |
    {
        type : "setWaitingWithMsg",
        msg : string
    }

function reducer(state: State, action: Action): State {
    switch(action.type) {
        case 'setWaiting' : {
            logger.info("setWaiting")
            return {type : 'waiting'}
        }
        case 'setWaitingWithMsg' : {
            logger.info("setWaitingWithMsg")
            return {type : 'waitingWithMsg', msg : action.msg}
        }
        case 'setPlaying' : {
            logger.info("setPlaying")
            return {type : 'playing', gameId : action.gameId}
        }
    }

}

export function Game() {
    const [state, dispatch] = React.useReducer(reducer, {type : 'starting'})    

    useEffect(() => {
        async function createGame() {
            if (state.type === "starting") { // start game
                const createGameResponse = await Services.createGame({
                    boardSize: 10,
                    fleet: {
                        BATTLESHIP: 4
                    },
                    nShotsPerRound: 1,
                    roundTimeout: 1000,
                })
                if (createGameResponse) {
                    if (typeof createGameResponse === 'string') {
                        dispatch({type:'setWaitingWithMsg', msg: createGameResponse})
                    } else {
                        if (createGameResponse.gameId) {
                            dispatch({type:'setPlaying', gameId: createGameResponse.gameId})
                        } else {
                            dispatch({type:'setWaiting'})
                        }
                    }
                }
            }
        }
        createGame()
    }, [state])

    if (state.type === "starting") {
        return <Starting />
    }
    if (state.type === "waiting") {
        return <Waiting />
    }
    if (state.type === "waitingWithMsg") {
        return <WaitingWithMsg msg={state.msg} />
    }
}

function Starting() {
    return (
        <div>
            <h1>Starting</h1>
        </div>
    )
}

function Waiting() {
    return (
        <div>
            <h1>Waiting</h1>
        </div>
    )
}

function WaitingWithMsg({msg} : {msg : string}) {
    return (
        <div>
            <h1>Waiting</h1>
            <p>{msg}</p>
        </div>
    )
}