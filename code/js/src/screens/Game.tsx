import * as React from 'react'
import {
    useReducer,
    useEffect,
} from 'react'
import { Services } from '../services'
import { CreateGameRequest, Game } from '../domain'
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
            switch(state.type) {
                case 'starting' : {
                    const createGameResponse = await Services.createGame({
                        boardSize: 10,
                        fleet: {
                            "CARRIER": 5,
                            "BATTLESHIP": 4,
                            "CRUISER": 3,
                            "SUBMARINE": 3,
                            "DESTROYER": 2
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
                case 'waiting' || 'waitingWithMsg' : {
                    const resp = await Services.getCurrentGameId()
                    if (typeof resp === 'number') {
                        dispatch({type:'setPlaying', gameId: resp})
                    } else {
                        dispatch({type:'setWaitingWithMsg', msg: resp as unknown as string})
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


async function getCurrentGameId(dispatch : React.Dispatch<Action>) {
    const resp = await Services.getCurrentGameId()
    if (typeof resp === 'number') {
        dispatch({type:'setPlaying', gameId: resp})
    } else {
        dispatch({type:'setWaitingWithMsg', msg: resp as unknown as string})
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
            <button type='button' onclick='getCurrentGameId()'>Refresh</button>
        </div>
    )
}

function Playing({game} : {game : Game}) {
    return (
        <div>
            <h1>Playing</h1>
            <p>{game.id}</p>
            <p>{game.player1}</p>
            <p>{game.player2}</p>
            <p>{game.state}</p>
        </div>
    )
}