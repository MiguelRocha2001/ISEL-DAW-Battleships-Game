import * as React from 'react'
import {useEffect, useState} from 'react'
import {Services} from '../services'
import {Board, Game} from '../domain'
import {Logger} from "tslog";
import styles from './Game.module.css'
import {useCurrentUser} from "./auth/Authn";


const logger = new Logger({ name: "GameScreen" });

type State =
    {
        type : "checkingForExistingOnGoingGame",
    }
    |
    {
        type : "menu",
        msg : string,
    }
    |
    {
        type : "creatingGame"
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
        type : "shooting",
        row : number,
        col : number
    }
    |
    {
        type : "updatingGameWhileNecessary",
        game : Game,
        msg : string
    }

type Action =
    {
        type : "setMenu",
        msg : string,
    }
    |
    {
        type : "setCreatingGame"
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
        type : "setShooting",
        row : number,
        col : number
    }
    |
    {
        type : "setUpdatingGameWhileNecessary",
        game : Game,
        msg : string
    }

function reducer(state: State, action: Action): State {
    switch(action.type) {
        case 'setMenu' : {
            logger.info("setStatic")
            return {type : 'menu', msg: action.msg}
        }
        case 'setCreatingGame' : {
            logger.info("setCreatingGame")
            return {type : 'creatingGame'}
        }
        case 'setPlaying' : {
            logger.info("setPlaying")
            return {type : 'playing', game : action.game, msg : null}
        }
        case 'setPlayingWithMsg' : {
            logger.info("setPlayingWithMsg")
            return {type : 'playing', game : action.game, msg : action.msg}
        }
        case 'setPlacingShips' : {
            logger.info("setPlacingShips")
            return {type : 'placingShips', row : action.row, col : action.col}
        }
        case 'setConfirmingFleet' : {
            logger.info("setConfirmingFleet")
            return {type : 'confirmingFleet'}
        }
        case 'setShooting' : {
            logger.info("setShooting")
            return {type : 'shooting', row : action.row, col : action.col}
        }
        case 'setUpdatingGameWhileNecessary' : {
            logger.info("setUpdatingGameWhileNecessary")
            return {type : 'updatingGameWhileNecessary', game : action.game, msg : action.msg}
        }
    }
}

export function Game() {
    const [state, dispatch] = React.useReducer(reducer, {type : 'checkingForExistingOnGoingGame'})
    const [selectedShip, setSelectedShip] = useState(null)
    const currentUser = useCurrentUser()

    async function checkForExistingOnGoingGame() {
        logger.info("checkingForExistingOnGoingGame")
        const resp = await Services.getGame(currentUser)
        if (typeof resp === 'string') {
            dispatch({type:'setMenu', msg: resp})
        } else {
            dispatch({type:'setUpdatingGameWhileNecessary', game: resp, msg : undefined})
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
        }, currentUser)
        if (typeof createGameResponse === 'string') {
            dispatch({type:'setMenu', msg: createGameResponse})
        } else dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg : 'Matchmaking'})
    }

    async function placeShip(row: number, col: number) {
        logger.info("placing ship in " + row + " " + col)
        if (!selectedShip) {
            logger.warn("no ship selected")
            dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: 'Please select a ship'})
            return
        }
        const ship = selectedShip as string
        if (ship) {
            const resp = await Services.placeShips({
                operation: "place-ships",
                ships: [
                    {
                        shipType: ship,
                        position: {row: row, column: col},
                        orientation: "HORIZONTAL"
                    }
                ],
                fleetConfirmed: false
            }, currentUser)
            if (typeof resp === 'string') {
                dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: resp})
            } else {
                dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: undefined})
            }
        }
    }

    async function confirmFleet() {
        logger.info("confirming fleet")
        const resp = await Services.confirmFleet(currentUser)
        if (typeof resp === 'string') {
            dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: resp})
        } else {
            dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: undefined})
        }
    }

    async function shoot(row: number, col: number) {
        logger.info("shooting in " + row + " " + col)
        const resp = await Services.attack(
            {shots: Array({row: row, column: col})},
            currentUser
        )
        if (typeof resp === 'string') {
            dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: resp})
        } else {
            dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: undefined})
        }
    }

    async function updateGameWhileNecessary() {
        function isMyTurn(game: Game) {
            const myPlayer = game.myPlayer
            const playerTurn = game.playerTurn
                if (playerTurn === game.player1 && myPlayer === 'one') return true
            return playerTurn === game.player2 && myPlayer === 'two';
        }

        logger.info("updateGameWhileNecessary")
        const resp = await Services.getGame(currentUser)
        if (typeof resp !== 'string') {
            if (resp.state != 'battle' || isMyTurn(resp)) {
                dispatch({type: 'setPlaying', game: resp})
                return
            }
            setTimeout(() => {
                dispatch({type: 'setUpdatingGameWhileNecessary', game: resp, msg: undefined})
            }, 1000)
        } else {
            setTimeout(() => {
                dispatch({type: 'setUpdatingGameWhileNecessary', game: undefined, msg: resp})
            }, 1000)
        }
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
                case 'updatingGameWhileNecessary': {
                    await updateGameWhileNecessary()
                    break
                }
                case 'placingShips' : {
                    await placeShip(state.row, state.col)
                    break
                }
                case 'confirmingFleet' : {
                    await confirmFleet()
                    break
                }
                case 'shooting' : {
                    await shoot(state.row, state.col)
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
            onUpdateRequest={() => dispatch({type : 'setUpdatingGameWhileNecessary', game : undefined, msg: undefined})}
        />
    } else if (state.type === "creatingGame") {
        return <CreatingGame />
    } else if (state.type === "playing") {
        return <Playing
            game={state.game}
            onPlaceShip={(row, col) => dispatch({type : 'setPlacingShips', row, col})}
            onShipChange={onShipChange}
            onConfirmFleetRequest={() => dispatch({type : 'setConfirmingFleet'})}
            onShot={(row, col) => dispatch({type : 'setShooting', row, col})}
            onUpdateRequest={() => dispatch({type : 'setUpdatingGameWhileNecessary', game: undefined, msg: undefined})}
        />
    } else if (state.type === "updatingGameWhileNecessary") {
        if (state.game) {
            return <Playing
                game={state.game}
                onPlaceShip={() => {}}
                onShipChange={() => {}}
                onConfirmFleetRequest={() => {}}
                onShot={() => {}}
                onUpdateRequest={() => {}}
            />
        } else {
            return (
                <div>
                    <h1>Updating Game</h1>
                    <p>{state.msg}</p>
                </div>
            )
        }
    } else if (state.type === "placingShips") {
        return (<div>Placing ships</div>)
    } else if (state.type === "confirmingFleet") {
        return (<div>Confirming fleet</div>)
    } else {
        return <div>Unknown state</div>
    }
}

function CheckingForSession() {
    return (
        <div>
            <h1>Checking Session</h1>
        </div>
    )
}

function NotLogged() {
    return (
        <div>
            <h1>Please Login before accessing your profile</h1>
        </div>
    )
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
        <div id = {styles.buttonsToPlay}>
            <p><button id={styles.newGame} className={styles.bigButton} role="button" onClick={onCreateGameRequest}>Start New Match</button></p>
            <p><button id={styles.joinGame} className={styles.bigButton} role="button" onClick={onUpdateRequest}>Resume Match</button></p>
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

function Playing({game, onPlaceShip, onShipChange, onConfirmFleetRequest, onShot, onUpdateRequest} : {
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

    let myBoard
    let enemyBoard
    if (game.myPlayer === "one") {
        myBoard = game.board1
        enemyBoard = game.board2
    }
    else {
        myBoard = game.board2
        enemyBoard = game.board1
    }

    const updateButton = <p><button onClick={onUpdateRequest}>Update Game</button></p>

    console.log('Game:', game)
    if (game.state === "fleet_setup") {
        return (
            <div>
                <FleetSetup board={myBoard} onPlaceShip={onPlaceShip} onShipChange={onShipChange} onConfirmFleet={onConfirmFleet}/>
                {updateButton}
            </div>
        )
    } else if (game.state === "battle") {
        return (
            <div>
                <Battle myBoard={myBoard} enemyBoard={enemyBoard} onShot={onShot}/>
                {updateButton}
            </div>
        )
    } else if (game.state === "finished") {
        return (
            <Finished winner={game.winner} />
        )
    }
}

function FleetSetup({board, onPlaceShip, onShipChange, onConfirmFleet}: {
    board : Board,
    onPlaceShip: (x : number, y : number) => void,
    onShipChange : (ship : string) => void,
    onConfirmFleet : () => void
}) {
    return (
        <div>
            <h1>Place Your Fleet</h1>
            <Board board={board} onCellClick={onPlaceShip}/>
            <ShipOptions onShipClick={onShipChange}/>
            <button onClick={onConfirmFleet}>Confirm Fleet</button>
        </div>
    )
}

function Battle({myBoard, enemyBoard, onShot} : {myBoard : Board, enemyBoard : Board, onShot : (x : number, y : number) => void}) {
    return (
        <div>
            <h1>Battle</h1>
            <div>
                <h2>My Board</h2>
                <Board board={myBoard} onCellClick={() => {}}/>
            </div>
            <div>
                <h2>Enemy Board</h2>
                <Board board={enemyBoard} onCellClick={onShot}/>
            </div>
        </div>
    )
}

function Finished({winner} : {winner : string}) {
    return (
        <div>
            <h1>Finished</h1>
            <p>Player {winner} has won</p>
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
                <input type="radio" value="CARRIER" name="gender" /> Carrier
                <input type="radio" value="BATTLESHIP" name="gender" /> Battleship
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
    const isHit = cell > 'a' && cell < 'z'
    const isWater = cell === ' '
    const color = isHit ? 'red' : isWater ? 'lightblue' : 'grey'
    if (isWater) {
        return (
            <button style={{backgroundColor: color}} onClick={onClick}>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </button>
        )
    } else {
        return (
            <button style={{backgroundColor: color}} onClick={onClick}>
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