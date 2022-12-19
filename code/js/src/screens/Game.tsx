import * as React from 'react'
import {
    useEffect, useState,
} from 'react'
import { Services } from '../services'
import {Board, Game, PlaceShipsRequest} from '../domain'
import { Logger } from "tslog";


const logger = new Logger({ name: "GameScreen" });

// TODO -> states that could have msg, should be one single state with msg that could be undefined
type State = 
    {
        type : "checkingForExistingOnGoingGame",
    }
    |
    {
        type : "menu"
    }
    |
    {
        type : "creatingGame",
        msg : string,
    }
    |
    {
        type : "matchmaking",
        msg : string,
    }
    |
    {
        type : "updatingGame",
        msg : string,
    }
    |
    {
        type : "playing",
        game: Game,
        msg : string
    }
    |
    {
        type : "placingShips",
        row : number,
        col : number
    }
    |
    {
        type : "confirmingFleet",
    }
    |
    {
        type : "updateGame",
        msg : string,
    }

type Action =     
    {
        type : "setMenu"
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
        type : "setPlayingWithMsg",
        game : Game,
        msg : string
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
    |
    {
        type : "setPlacingShips",
        row : number,
        col : number
    }
    |
    {
        type : "setConfirmingFleet"
    }
    |
    {
        type : "setUpdateGame"
    }
    |
    {
        type : "setUpdateGameWithMsg",
        msg : string
    }

function reducer(state: State, action: Action): State {
    switch(action.type) {
        case 'setMenu' : {
            logger.info("setStatic")
            return {type : 'menu'}
        }
        case 'setMatchmaking' : {
            logger.info("setMatchmaking")
            return {type : 'matchmaking', msg : null}
        }
        case 'setMatchmakingWithMsg' : {
            logger.info("setMatchmakingWithMsg")
            return {type : 'matchmaking', msg : action.msg}
        }
        case 'setCreatingGame' : {
            logger.info("setCreatingGame")
            return {type : 'creatingGame', msg : null}
        }
        case 'setCreatingGameWithMsg' : {
            logger.info("setCreatingGameWithMsg")
            return {type : 'creatingGame', msg : action.msg}
        }
        case 'setPlaying' : {
            logger.info("setPlaying")
            return {type : 'playing', game : action.game, msg : null}
        }
        case 'setPlayingWithMsg' : {
            logger.info("setPlayingWithMsg")
            return {type : 'playing', game : action.game, msg : action.msg}
        }
        case 'setUpdatingGame' : {
            logger.info("setUpdatingGame")
            return {type : 'updatingGame', msg : null}
        }
        case 'setUpdatingWithMsg' : {
            logger.info("setUpdatingWithMsg")
            return {type : 'updatingGame', msg : action.msg}
        }
        case 'setPlacingShips' : {
            logger.info("setPlacingShips")
            return {type : 'placingShips', row : action.row, col : action.col}
        }
        case 'setConfirmingFleet' : {
            logger.info("setConfirmingFleet")
            return {type : 'confirmingFleet'}
        }
        case 'setUpdateGame' : {
            logger.info("setUpdateGame")
            return {type : 'updateGame', msg : null}
        }
        case 'setUpdateGameWithMsg' : {
            logger.info("setUpdateGameWithMsg")
            return {type : 'updateGame', msg : action.msg}
        }
    }
}

export function Game() {
    const [state, dispatch] = React.useReducer(reducer, {type : 'checkingForExistingOnGoingGame'})
    const [selectedShip, setSelectedShip] = useState(null)

    async function checkForExistingOnGoingGame() {
        logger.info("checkingForExistingOnGoingGame")
        const resp = await Services.getGame()
        if (typeof resp === 'string') {
            dispatch({type:'setMenu'})
        } else {
            dispatch({type:'setPlaying', game: resp})
        }
    }

    async function updateGame() {
        logger.info("updatingGame")
        const resp = await Services.getGame()
        if (typeof resp === 'string') {
            dispatch({type:'setUpdatingWithMsg', msg: resp as unknown as string})
        } else {
            dispatch({type:'setPlaying', game: resp})
        }
    }

    async function createGame() {
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
    }

    async function placeShip(row: number, col: number) {
        logger.info("placing ship in " + row + " " + col)
        if (!selectedShip) {
            logger.warn("no ship selected")
            return
        }
        const ship = selectedShip as string
        if (ship) {
            const resp = await Services.placeShips({
                operation: "place-ships",
                ships: [
                    {
                        shipType: ship, // TODO uppercase
                        position: {row: row, column: col},
                        orientation: "HORIZONTAL"
                    }
                ],
                fleetConfirmed: false
            })
            if (typeof resp === 'string') {
                dispatch({type:'setUpdateGameWithMsg', msg: resp})
            } else {
                dispatch({type:'setUpdateGame'})
            }
        }
    }

    async function confirmFleet() {
        logger.info("confirming fleet")
        const resp = await Services.confirmFleet()
        if (typeof resp === 'string') {
            dispatch({type:'setUpdateGameWithMsg', msg: resp})
        } else {
            dispatch({type:'setUpdateGame'})
        }
    }

    async function shoot(row: number, col: number) {
        logger.info("shooting in " + row + " " + col)
        /*
        const resp = await Services.placeShip()
         */
    }

    function onShipChange(ship: string) {
        logger.info("ship " + ship + " selected")
        setSelectedShip(ship)
    }

    useEffect(() => {
        async function stateMachineHandler() {
            switch(state.type) {
                case 'checkingForExistingOnGoingGame' : {
                    await checkForExistingOnGoingGame()
                    break
                }
                case 'creatingGame' : {
                    await createGame()
                    break
                }
                case 'updatingGame' : {
                    await updateGame()
                    break
                }
                case 'placingShips' : {
                    await placeShip(state.row, state.col)
                }
            }
        }
        stateMachineHandler()
    }, [state])

    if (state.type === "checkingForExistingOnGoingGame") {
        return <CheckingForExistingOnGoingGame />
    } if (state.type === "menu") {
        return <Menu
            onCreateGameRequest={() => dispatch({type : 'setCreatingGame'})}
            onUpdateRequest={() => dispatch({type : 'setUpdatingGame'})}
        />
    } else if (state.type === "creatingGame") {
        return <CreatingGame />
    } else if (state.type === "matchmaking") {
        return <Matchmaking onUpdateRequest={() => dispatch({type : 'setUpdatingGame'})} msg={state.msg} />
    } else if (state.type === "playing") {
        return <Playing
            game={state.game}
            onPlaceShip={(row, col) => dispatch({type : 'setPlacingShips', row, col})}
            onShipChange={onShipChange}
            onConfirmFleetRequest={confirmFleet}
            onShot={shoot}
            onUpdateRequest={() => dispatch({type : 'setUpdatingGame'})}
        />
    } else if (state.type === "updatingGame") {
        return <UpdatingGame msg={state.msg}/>
    } else if (state.type === "placingShips") {
        return (<div>Placing ships</div>)
    } else if (state.type === "confirmingFleet") {
        return (<div>Confirming fleet</div>)
    } else if (state.type === "updateGame") {
        return <UpdateGame msg={state.msg} onUpdateRequest={() => dispatch({type : 'setUpdatingGame'})}/>
    }
    else {
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

function Menu({onCreateGameRequest, onUpdateRequest} : {onCreateGameRequest : () => void, onUpdateRequest : () => void}) {
    return (
        <div>
            <h1>Menu</h1>
            <p><button onClick={onCreateGameRequest}>Create New Game</button></p>
            <p><button onClick={onUpdateRequest}>Update Game</button></p>
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

function Matchmaking({msg, onUpdateRequest} : {msg : string, onUpdateRequest : () => void}) {
    const mainContent =
        <div>
            <h1>Waiting</h1>
            <p><button onClick={onUpdateRequest}>Update Game</button></p>
        </div>
    if (msg) {
        return (
            <div>
                {mainContent}
                <ErrorMsg msg={msg}/>
            </div>
        )
    } else {
        return mainContent
    }
}

function UpdatingGame({msg} : {msg : string}) {
    const mainContent =
        <div>
            <h1>Updating</h1>
        </div>
    if (msg) {
        return (
            <div>
                {mainContent}
                <ErrorMsg msg={msg}/>
            </div>
        )
    } else {
        return mainContent
    }
}

function UpdateGame({msg, onUpdateRequest} : {msg: string, onUpdateRequest : () => void}) {
    const mainContent =
        <div>
            <h1>Update Game</h1>
            <p><button onClick={onUpdateRequest}>Update Game</button></p>
        </div>
    if (msg) {
        return (
            <div>
                {mainContent}
                <ErrorMsg msg={msg}/>
            </div>
        )
    }
}

function Playing({game, onPlaceShip, onShipChange, onConfirmFleetRequest, onShot, onUpdateRequest} :
                     {
                         game : Game,
                         onPlaceShip : (x : number, y : number) => void,
                         onShipChange : (ship: string) => void,
                         onConfirmFleetRequest : () => void,
                         onShot : (x : number, y : number) => void,
                            onUpdateRequest : () => void
                     }) {

    function onConfirmFleet() {
        onShipChange(null)
        onConfirmFleetRequest()
    }

    const title = <h1>Phase: {game.state}</h1>

    console.log('Game:', game)
    if (game.state === "fleet_setup") {
        let boardComponent
        if (game.myPlayer === "one")
            boardComponent = <Board board={game.board1} onCellClick={onPlaceShip} />
        else
            boardComponent = <Board board={game.board2} onCellClick={onPlaceShip} />
        return (
            <div>
                {title}
                {boardComponent}
                <ShipOptions onShipClick={onShipChange}/>
                <button onClick={onConfirmFleet}>Confirm Fleet</button>
                <p><button onClick={onUpdateRequest}>Update Game</button></p>
            </div>
        )
    } else if (game.state === "battle") {
        // TODO display opponent board
    } else if (game.state === "finished") {
        // TODO display winner
    }
}

function ShipOptions({onShipClick} : {onShipClick : (ship : string) => void}) {
    function onChangeValue(event) {
        onShipClick(event.target.value)
    }
    return (
        <div>
            <h1>ShipOptions</h1>
            <div onChange={onChangeValue}>
                <input type="radio" value="CARRIER" name="gender" /> Carrier
                <input type="radio" value="BATTLESHIP" name="gender" /> Battleships
                <input type="radio" value="CRUISER" name="gender" /> Cruiser
                <input type="radio" value="SUBMARINE" name="gender" /> Submarine
                <input type="radio" value="DESTROYER" name="gender" /> Destroyer
            </div>
        </div>
    )
}

function Board({board, onCellClick} : {board : Board, onCellClick? : (row: number, col: number) => void}) {
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
                                            <Cell cell={cell} onClick={() => { onCellClick(row + 1, coll + 1) }} />
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

function ErrorMsg({msg} : {msg : string}) {
    return (
        <div>
            <p style={{color: "red"}}>{msg}</p>
        </div>
    )
}
