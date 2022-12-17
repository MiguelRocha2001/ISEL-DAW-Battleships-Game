import * as React from 'react'
import {
    useEffect, useState,
} from 'react'
import { Services } from '../services'
import { Board, Game } from '../domain'
import { Logger } from "tslog";


const logger = new Logger({ name: "GameScreen" });

type State = 
    {
        type : "checkingForExistingOnGoingGame",
    }
    |
    {
        type : "static"
    }
    |
    {
        type : "creatingGame",
    }
    |
    {
        type : "creatingGameWithMsg",
        msg : string
    }
    |
    {
        type : "matchmaking"
    }
    |
    {
        type : "matchmakingWithMsg",
        msg : string
    }
    |
    {
        type : "updatingGame"
    }
    |
    {
        type : "updatingWithMsg",
        msg : string
    }
    |
    {
        type : "playing",
        game: Game
    }

type Action =     
    {
        type : "setStatic"
    }
    |
    {
        type : "setCreatingGame"
    }
    |
    {
        type : "setCreatingGameWithMsg",
        msg : string
    }
    |
    {
        type : "setMatchmaking"
    }
    |
    {
        type : "setMatchmakingWithMsg",
        msg : string
    }
    |
    {
        type : "setPlaying",
        game : Game
    }
    |
    {
        type : "setUpdatingGame"
    }
    |
    {
        type : "setUpdatingWithMsg",
        msg : string
    }

function reducer(state: State, action: Action): State {
    switch(action.type) {
        case 'setStatic' : {
            logger.info("setStatic")
            return {type : 'static'}
        }
        case 'setMatchmaking' : {
            logger.info("setMatchmaking")
            return {type : 'matchmaking'}
        }
        case 'setMatchmakingWithMsg' : {
            logger.info("setMatchmakingWithMsg")
            return {type : 'matchmakingWithMsg', msg : action.msg}
        }
        case 'setCreatingGame' : {
            logger.info("setCreatingGame")
            return {type : 'creatingGame'}
        }
        case 'setCreatingGameWithMsg' : {
            logger.info("setCreatingGameWithMsg")
            return {type : 'creatingGameWithMsg', msg : action.msg}
        }
        case 'setPlaying' : {
            logger.info("setPlaying")
            return {type : 'playing', game : action.game}
        }
        case 'setUpdatingGame' : {
            logger.info("setUpdatingGame")
            return {type : 'updatingGame'}
        }
        case 'setUpdatingWithMsg' : {
            logger.info("setUpdatingWithMsg")
            return {type : 'updatingWithMsg', msg : action.msg}
        }
    }
}

export function Game() {
    const [state, dispatch] = React.useReducer(reducer, {type : 'checkingForExistingOnGoingGame'})
    const [selectedShip, setSelectedShip] = useState(null)

    async function onCellTouch(row: number, col: number) {
        logger.info("cell (" + row + ", " + col + ") touched")
        /*
        const resp = await Services.placeShip()
        if (typeof resp === 'string') {
            dispatch({type:'setStatic'})
        } else {
            dispatch({type:'setPlaying', game: resp})
        }
         */
    }

    async function onShipChange(ship: string) {
        logger.info("ship " + ship + " selected")
        setSelectedShip(ship)
    }

    useEffect(() => {
        async function stateMachineHandler() {
            switch(state.type) {
                case 'checkingForExistingOnGoingGame' : {
                    logger.info("checkingForExistingOnGoingGame")
                    const resp = await Services.getGame()
                    if (typeof resp === 'string') {
                        dispatch({type:'setStatic'})
                    } else {
                        dispatch({type:'setPlaying', game: resp})
                    }
                    break
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
                        shots: 1,
                        roundTimeout: 200,
                    })
                    if (createGameResponse) {
                        if (typeof createGameResponse === 'string') {
                            dispatch({type:'setCreatingGameWithMsg', msg: createGameResponse})
                        } else dispatch({type:'setMatchmaking'})
                    }
                    break
                }
                case 'updatingGame' : {
                    logger.info("updatingGame")
                    const resp = await Services.getGame()
                    if (typeof resp === 'string') {
                        dispatch({type:'setUpdatingWithMsg', msg: resp as unknown as string})
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
    } if (state.type === "static") {
        return <Static dispatch={dispatch} />
    } else if (state.type === "creatingGame") {
        return <CreatingGame />
    } else if (state.type === "matchmaking") {
        return <Matchmaking dispatch={dispatch} />
    } else if (state.type === "matchmakingWithMsg") {
        return <MatchmakingWithMsg dispatch={dispatch} errMsg={state.msg} />
    } else if (state.type === "playing") {
        return <Playing game={state.game} onCellTouch={onCellTouch} onShipChange={onShipChange}/>
    } else if (state.type === "updatingGame") {
        return <UpdatingGame />
    } else if (state.type === "updatingWithMsg") {
        return <UpdatingWithMsg errMsg={state.msg} />
    } else {
        return <div>Unknown state</div>
    }
}

function CheckingForExistingOnGoingGame() {
    return (
        <div>
            <h1>Checking for existing game</h1>
        </div>
    )
}

function Static({dispatch} : {dispatch: React.Dispatch<Action>}) {
    function createNewGame() {
        dispatch({type:'setCreatingGame'})
    }
    function updateGame() {
        dispatch({type:'setUpdatingGame'})
    }
    return (
        <div>
            <h1>Static</h1>
            <p><button onClick={createNewGame}>Create New Game</button></p>
            <p><button onClick={updateGame}>Update Game</button></p>
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

function Matchmaking({dispatch} : {dispatch: React.Dispatch<Action>}) {
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

function MatchmakingWithMsg({dispatch, errMsg} : {dispatch: React.Dispatch<Action>, errMsg : string}) {
    function updateGame() {
        dispatch({type:'setUpdatingGame'})
    }
    return (
        <div>
            <h1>Waiting</h1>
            <p>{errMsg}</p>
            <p><button onClick={updateGame}>Update Game</button></p>
        </div>
    )
}

function UpdatingGame() {
    return (
        <div>
            <h1>Updating</h1>
        </div>
    )
}

function UpdatingWithMsg({errMsg} : {errMsg : string}) {
    return (
        <div>
            <h1>Updating</h1>
            <p>{errMsg}</p>
        </div>
    )
}

function Playing(
    {game, onCellTouch, onShipChange}
        : {game : Game, onCellTouch : (x : number, y : number) => void, onShipChange : (ship: string) => void}) {

    function setShip(ship : string) {
        onShipChange(ship)
    }

    return (
        <div>
            <h1>Playing</h1>
            <p>{game.id}</p>
            <p>{game.player1}</p>
            <p>{game.player2}</p>
            <p>{game.state}</p>
            <Board board={game.board1} onClick={onCellTouch}/>
            <ShipOptions onShipClick={setShip}/>
        </div>
    )
}

function ShipOptions({onShipClick} : {onShipClick : (ship : string) => void}) {
    function onChangeValue(event) {
        onShipClick(event.target.value)
    }
    return (
        <div>
            <h1>ShipOptions</h1>
            <div onChange={onChangeValue}>
                <input type="radio" value="Carrier" name="gender" /> Carrier
                <input type="radio" value="Battleships" name="gender" /> Battleships
                <input type="radio" value="Cruiser" name="gender" /> Cruiser
                <input type="radio" value="Submarine" name="gender" /> Submarine
                <input type="radio" value="Destroyer" name="gender" /> Destroyer
            </div>
        </div>
    )
}

function Board({board, onClick} : {board : Board, onClick? : (row: number, col: number) => void}) {
    console.log(board.cells)
    const boardStr = board.cells
    const rowNumber = Math.sqrt(board.ncells)
    const collNumber = rowNumber
    return (
        <div>
            <h1>Board</h1>
            <table>
                <tbody>
                    {Array.from(Array(rowNumber).keys()).map((row) => {
                        return (
                            <tr key={row}>
                                {Array.from(Array(collNumber).keys()).map((coll) => {
                                    const cell = boardStr[row * rowNumber + coll]
                                    return (
                                        <td key={coll}>
                                            <Cell cell={cell} onClick={() => { onClick(row, coll) }} />
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

function Cell({cell, onClick} : {cell : string, onClick? : () => void}) {
    const isWater = cell === ' '
    if (isWater) {
        return (
            <button style={{backgroundColor: "lightblue"}} onClick={onClick}>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </button>
        )
    } else {
        return (
            <button style={{backgroundColor: "grey"}} onClick={onClick}>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </button>
        )
    }
}