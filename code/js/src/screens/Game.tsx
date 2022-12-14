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
        type : "updatingGame"
    }
    |
    {
        type : "playing",
        game: Game
    }

type Action =     
    {
        type : "setWaiting"
    }
    |
    {
        type : "setPlaying",
        game : Game
    }
    |
    {
        type : "setWaitingWithMsg",
        msg : string
    }
    |
    {
        type : "setUpdatingGame"
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
            return {type : 'playing', game : action.game}
        }
        case 'setUpdatingGame' : {
            logger.info("setUpdatingGame")
            return {type : 'updatingGame'}
        }
    }

}

export function Game() {
    const [state, dispatch] = React.useReducer(reducer, {type : 'starting'})    

    useEffect(() => {
        async function stateMachineHandler() {
            switch(state.type) {
                case 'starting' : {
                    logger.info("starting")
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
                        } else dispatch({type:'setWaiting'})
                    }
                }
                case 'updatingGame' : {
                    logger.info("updatingGame")
                    const resp = await Services.getGame()
                    if (typeof resp === 'string') {
                        dispatch({type:'setWaitingWithMsg', msg: resp as unknown as string})
                    } else {
                        dispatch({type:'setPlaying', game: resp})
                    }
                }
            }
        }
        stateMachineHandler()
    }, [state])

    if (state.type === "starting") {
        return <Starting />
    }
    if (state.type === "waiting") {
        return <Waiting dispatch={dispatch} />
    }
    if (state.type === "waitingWithMsg") {
        return <WaitingWithMsg dispatch={dispatch} msg={state.msg} />
    }
}

function Starting() {
    return (
        <div>
            <h1>Starting</h1>
        </div>
    )
}

function Waiting({dispatch} : {dispatch: React.Dispatch<Action>}) {
    function updateGame() {
        dispatch({type:'setUpdatingGame'})
    }
    return (
        <div>
            <h1>Waiting</h1>
            <p><button onClick={updateGame}>Update Game</button></p>
        </div>
    )
}

function WaitingWithMsg({dispatch, msg} : {dispatch: React.Dispatch<Action>, msg : string}) {
    function updateGame() {
        dispatch({type:'setUpdatingGame'})
    }
    return (
        <div>
            <h1>Waiting</h1>
            <p>{msg}</p>
            <p><button onClick={updateGame}>Update Game</button></p>
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