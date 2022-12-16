import * as React from 'react'
import {
    useReducer,
    useEffect,
} from 'react'
import { Services } from '../services'
import { Board, CreateGameRequest, Game } from '../domain'
import { Logger } from "tslog";


const logger = new Logger({ name: "GameScreen" });

type State = 
    {
        type : "checkingForExistingOnGoingGame",
    }
    |
    {
        type : "creatingGame",
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
        type : "setCreatingGame"
    }
    |
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
    const [state, dispatch] = React.useReducer(reducer, {type : 'checkingForExistingOnGoingGame'})    

    useEffect(() => {
        async function stateMachineHandler() {
            switch(state.type) {
                case 'checkingForExistingOnGoingGame' : {
                    logger.info("checkingForExistingOnGoingGame")
                    const resp = await Services.getGame()
                    if (typeof resp === 'string') {
                        dispatch({type:'setWaitingWithMsg', msg: resp as unknown as string})
                    } else {
                        dispatch({type:'setPlaying', game: resp})
                    }
                }
                case 'creatingGame' : {
                    logger.info("creatingGame")
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

    if (state.type === "checkingForExistingOnGoingGame") {
        return <CheckingForExistingOnGoingGame />
    } else if (state.type === "creatingGame") {
        return <CreatingGame />
    } else if (state.type === "waiting") {
        return <Waiting dispatch={dispatch} />
    } else if (state.type === "waitingWithMsg") {
        return <Waiting dispatch={dispatch} errMsg={state.msg} />
    } else if (state.type === "playing") {
        return <Playing game={state.game} />
    }
}

function CheckingForExistingOnGoingGame() {
    return (
        <div>
            <h1>Checking for existing game</h1>
        </div>
    )
}

function CreatingGame() {
    return (
        <div>
            <h1>Starting</h1>
        </div>
    )
}

function Waiting({dispatch, errMsg} : {dispatch: React.Dispatch<Action>, errMsg? : string}) {
    function updateGame() {
        dispatch({type:'setUpdatingGame'})
    }
    function createNewGame() {
        dispatch({type:'setCreatingGame'})
    }
    return (
        <div>
            <h1>Waiting</h1>
            <p>{errMsg}</p>
            <p><button onClick={updateGame}>Update Game</button></p>
            <p><button onClick={createNewGame}>Create New Game</button></p>
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
            <Board board={game.board1} />
        </div>
    )
}

function Board({board} : {board : Board}) {
    const boardStr = board.cells
    const rowNumber = Math.sqrt(board.ncells)
    const collNumber = rowNumber
    return (
        <div>
            <table>
                <tbody>
                    {Array.from(Array(rowNumber).keys()).map((row) => {
                        return (
                            <tr key={row}>
                                {Array.from(Array(collNumber).keys()).map((coll) => {
                                    const cell = boardStr[row * rowNumber + coll]
                                    return (
                                        <td key={coll}>
                                            {cell}
                                        </td>
                                    )
                                })}
                            </tr>
                        )
                    })}
                </tbody>
            </table>
        </div>
    )
}
