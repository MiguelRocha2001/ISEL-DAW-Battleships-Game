import * as React from 'react'
import {useEffect, useState} from 'react'
import {Services} from '../services'
import {Board, Game, Orientation} from '../domain'
import {Logger} from "tslog";
import styles from './Game.module.css'
import {useCurrentUser} from "./auth/Authn";
import {Loading} from "./Loading";
import {Button} from "react-bootstrap";


const logger = new Logger({ name: "GameComponent" });

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
        ship : string,
        row : number,
        col : number,
        orient : Orientation,
    }
    |
    {
        type : "confirmingFleet",
    }
    |
    {
        type : "waitingForConfirmation"
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
    |
    {
        type : "quitGame",
        gameId : number
    }

type Action =
    {
        type : "setCheckForExistingOnGoingGame",
    }
    |
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
        ship : string,
        row : number,
        col : number,
        orient : Orientation,
    }
    |
    {
        type : "setConfirmingFleet"
    }
    |
    {
        type : "setWaitingForConfirmation"
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
    |
    {
        type : "setQuitGame",
        gameId : number
    }

function reducer(state: State, action: Action): State {
    switch(action.type) {
        case "setCheckForExistingOnGoingGame" : {
            logger.info("setCheckForExistingOnGoingGame")
            return {type: "checkingForExistingOnGoingGame"}
        }
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
            return {type : 'placingShips', ship : action.ship, row : action.row, col : action.col, orient : action.orient}
        }
        case 'setConfirmingFleet' : {
            logger.info("setConfirmingFleet")
            return {type : 'confirmingFleet'}
        }
        case 'setWaitingForConfirmation' : {
            logger.info("setWaitingForConfirmation")
            return {type : 'waitingForConfirmation'}
        }
        case 'setShooting' : {
            logger.info("setShooting")
            return {type : 'shooting', row : action.row, col : action.col}
        }
        case 'setUpdatingGameWhileNecessary' : {
            logger.info("setUpdatingGameWhileNecessary")
            return {type : 'updatingGameWhileNecessary', game : action.game, msg : action.msg}
        }
        case 'setQuitGame' : {
            logger.info("setQuitGame")
            return {type : 'quitGame', gameId : action.gameId}
        }
    }
}

export function Game() {
    const [state, dispatch] = React.useReducer(reducer, {type : 'checkingForExistingOnGoingGame'})
    const currentUser = useCurrentUser()
    let cancelRequest = false

    async function checkForExistingOnGoingGame() {
        logger.info("checkingForExistingOnGoingGame")
        const resp = await Services.getGame(currentUser)
        if (cancelRequest) {
            logger.info("checkForExistingOnGoingGame cancelled")
            return
        }
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
        if (cancelRequest) {
            logger.info("createGame cancelled")
            return
        }
        if (typeof createGameResponse === 'string') {
            if (createGameResponse === 'User already in a queue') {
                dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg : 'Matchmaking'})
            } else {
                dispatch({type:'setMenu', msg: createGameResponse})
            }
        } else dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg : 'Matchmaking'})
    }


    async function placeShip(ship: string, row: number, col: number, orientation: Orientation) {
        logger.info("placingShip: " + ship + " at " + row + "," + col + " " + orientation)
        if (ship) {
            const resp = await Services.placeShips({
                operation: "place-ships",
                ships: [
                    {
                        shipType: ship,
                        position: {row: row, column: col},
                        orientation: orientation
                    }
                ],
                fleetConfirmed: false
            }, currentUser)
            if (cancelRequest) {
                logger.info("placeShip cancelled")
                return
            }
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
        if (cancelRequest) {
            logger.info("confirmFleet cancelled")
            return
        }
        if (typeof resp === 'string') {
            dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: resp})
        } else {
            dispatch({type:'setWaitingForConfirmation'})
        }
    }

    async function shoot(row: number, col: number) {
        logger.info("shooting in " + row + " " + col)
        const resp = await Services.attack(
            {shots: Array({row: row, column: col})},
            currentUser
        )
        if (cancelRequest) {
            logger.info("shoot cancelled")
            return
        }
        if (typeof resp === 'string') {
            dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: resp})
        } else {
            dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: undefined})
        }
    }

    async function updateUntilConfirmation() {
        logger.info("updatingUntilConfirmation")
        const resp = await Services.getGame(currentUser)
        if (cancelRequest) {
            logger.info("updateUntilConfirmation cancelled")
            return
        }
        if (typeof resp === 'string') {
            dispatch({type:'setWaitingForConfirmation'})
        } else {
            const player = resp.myPlayer
            const myBoard = player === 'one' ? resp.board1 : resp.board2
            if (myBoard.isConfirmed) {
                dispatch({type: 'setUpdatingGameWhileNecessary', game: undefined, msg: undefined})
            } else {
                dispatch({type:'setWaitingForConfirmation'})
            }
        }
    }

    /**
     * Fetches game from data and updates state accordingly.
     */
    async function updateGameWhileNecessary() {
        function isMyTurn(game: Game) {
            const myPlayer = game.myPlayer
            const playerTurn = game.playerTurn
                if (playerTurn === game.player1 && myPlayer === 'one') return true
            return playerTurn === game.player2 && myPlayer === 'two';
        }

        logger.info("updateGameWhileNecessary")
        const resp = await Services.getGame(currentUser)
        if (cancelRequest) {
            logger.info("updateGameWhileNecessary cancelled")
            return
        }
        if (typeof resp !== 'string') {
            if (resp.state !== 'battle' || isMyTurn(resp)) {
                if (resp.state === 'fleet_setup') {
                    const player = resp.myPlayer
                    const myBoard = player === 'one' ? resp.board1 : resp.board2
                    const enemyBoard = player === 'one' ? resp.board2 : resp.board1
                    if (myBoard.isConfirmed && !enemyBoard.isConfirmed) {
                        setTimeout(() => {
                            dispatch({type: 'setUpdatingGameWhileNecessary', game: resp, msg: 'Waiting for opponent'})
                        }, 1000)
                        return
                    }
                }
                dispatch({type: 'setPlaying', game: resp})
                return
            }
            setTimeout(() => {
                dispatch({type: 'setUpdatingGameWhileNecessary', game: resp, msg: 'Waiting for opponent'})
            }, 1000)
        } else {
            let msg;
            if (state.type === 'updatingGameWhileNecessary' && state.msg === 'Matchmaking') {
                msg = 'Matchmaking'
            } else {
                msg = undefined
            }
            setTimeout(() => {
                dispatch({type: 'setUpdatingGameWhileNecessary', game: undefined, msg: msg})
            }, 1000)
        }
    }

    async function quitGame(gameId: number) {
        logger.info("quittingGame")
        const resp = await Services.quitGame(gameId, currentUser)
        if (cancelRequest) {
            logger.info("quitGame cancelled")
            return
        }
        if (typeof resp === 'string') {
            dispatch({type:'setMenu', msg: resp})
        } else {
            dispatch({type:'setMenu', msg: undefined})
        }
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
                    await placeShip(state.ship, state.row, state.col, state.orient)
                    break
                }
                case 'confirmingFleet' : {
                    await confirmFleet()
                    break
                }
                case 'waitingForConfirmation' : {
                    await updateUntilConfirmation()
                    break
                }
                case 'shooting' : {
                    await shoot(state.row, state.col)
                    break
                }
                case 'quitGame' : {
                    await quitGame(state.gameId)
                    break
                }
            }
        }
        stateMachineHandler()
        return () => {
            cancelRequest = true
            logger.info("Game unmounted")
        };
    }, [state])

    if (state.type === "checkingForExistingOnGoingGame") {
        return <CheckingForExistingOnGoingGame />
    } if (state.type === "menu") {
        return <Menu onCreateGameRequest={() => dispatch({type : 'setCreatingGame'})}/>
    } else if (state.type === "creatingGame") {
        return <CreatingGame />
    } else if (state.type === "playing") {
        return <Playing
            game={state.game}
            onPlaceShip={(ship, row, col, orient) => dispatch({type : 'setPlacingShips', ship, row, col, orient})}
            onConfirmFleetRequest={() => dispatch({type : 'setConfirmingFleet'})}
            onShot={(row, col) => dispatch({type : 'setShooting', row, col})}
            onQuitRequest={(gameId: number) => dispatch({type : 'setQuitGame', gameId})}
        />
    } else if (state.type === "updatingGameWhileNecessary") {
        const title = state.msg ? state.msg : "Updating game"
        if (state.game) {
            return (
                <div>
                    <h1>{title}</h1>
                    <Playing
                        game={state.game}
                        onPlaceShip={() => {}}
                        onConfirmFleetRequest={() => {}}
                        onShot={() => {}}
                        onQuitRequest={() => {}}
                    />
                </div>
            )
        } else {
            return (
                <div>
                    <h1>{title}</h1>
                    <Loading />
                </div>
            )
        }
    } else if (state.type === "waitingForConfirmation") {
        return (
            <div>
                <h1>Waiting For Confirmation</h1>
                <Loading />
            </div>
        )
    } else if (state.type === "placingShips") {
        return (<div>Placing ships</div>)
    } else if (state.type === "confirmingFleet") {
        return (<div>Confirming fleet</div>)
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

function Menu({onCreateGameRequest} : {onCreateGameRequest : () => void}) {
    return (
        <div id = {styles.buttonsToPlay}>
            <button id={styles.newGame} className={styles.cybrBtn} onClick={onCreateGameRequest}>
                Create New<span aria-hidden>_</span>
                <span aria-hidden className={styles.cybrbtn__glitch}>Create New</span>
            </button>
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

function Playing({game, onPlaceShip, onConfirmFleetRequest, onShot, onQuitRequest} : {
    game : Game,
    onPlaceShip : (ship: string, x : number, y : number, o: Orientation) => void,
    onConfirmFleetRequest : () => void,
    onShot : (x : number, y : number) => void,
    onQuitRequest : (gameId: number) => void
 }) {

    function onConfirmFleet() {
        onConfirmFleetRequest()
    }

    let myBoard: Board
    let fleetConfirmed: boolean
    let enemyBoard: Board
    let winner: string | undefined
    if (game.myPlayer === "one") {
        myBoard = game.board1
        fleetConfirmed = game.board1.isConfirmed
        enemyBoard = game.board2
        if (game.winner == game.player1)
            winner = "You"
        else if (game.winner == game.player2)
            winner = "Opponent"
    }
    else {
        myBoard = game.board2
        fleetConfirmed = game.board2.isConfirmed
        enemyBoard = game.board1
        if (winner && game.winner == game.player2)
            winner = "You"
        else if (winner && game.winner == game.player1)
            winner = "Opponent"
    }

    const quitButton = <Button onClick={() => {onQuitRequest(game.id)}} variant="contained" color="secondary">Quit</Button>

    if (game.state === "fleet_setup") {
        return (
            <div>
                <FleetSetup
                    board={myBoard}
                    fleetConfirmed={fleetConfirmed}
                    onPlaceShip={(ship, x, y, o) => onPlaceShip(ship, x, y, o)}
                    onConfirmFleet={onConfirmFleet}
                />
                {quitButton}
            </div>
        )
    } else if (game.state === "battle") {
        return (
            <div>
                <Battle myBoard={myBoard} enemyBoard={enemyBoard} onShot={onShot}/>
                {quitButton}
            </div>
        )
    } else if (game.state === "finished") {
        return (
            <Finished winner={winner} />
        )
    }
}

function FleetSetup({board, fleetConfirmed, onPlaceShip, onConfirmFleet}: {
    board : Board,
    fleetConfirmed : boolean,
    onPlaceShip : (ship: string, x : number, y : number, o: Orientation) => void,
    onConfirmFleet : () => void
}) {
    const [selectedShip, setSelectedShip] = useState<string>(null)
    const [selectedOrientation, setSelectedOrientation] = useState<Orientation>('HORIZONTAL')

    function onConfirmFleetAux() {
        setSelectedShip(null)
        onConfirmFleet()
    }

    function onOrientationChange() {
        if (selectedOrientation === "HORIZONTAL") setSelectedOrientation("VERTICAL")
        else setSelectedOrientation("HORIZONTAL")
    }

    function placeShipIfNecessary(x : number, y : number) {
        if (fleetConfirmed != true && selectedShip != null) {
            onPlaceShip(selectedShip, x, y, selectedOrientation)
            setSelectedShip(null)
        }
    }

    const title = fleetConfirmed == false ? "Await For Opponent" : "Place your ships"

    const options = fleetConfirmed == false ? (
        <div className={styles.right}>
            <button className={styles.cybrBtn} onClick={onConfirmFleetAux}>
                Ready!<span aria-hidden>_</span>
                <span aria-hidden className={styles.cybrbtn__glitch}>Ready</span>
            </button>
            <div id={styles.buttonsForSelectShips}>
                <ShipOptions
                    curOrientation={selectedOrientation}
                    onShipClick={(ship: string) => setSelectedShip(ship)}
                    onOrientationChange={onOrientationChange}
                />
            </div>
        </div>
    ) : null
    return (
        <div className={styles.fullWidth}>
            <p><h1 className={styles.h1}>{title}</h1></p>
            <div className={styles.left}>
                <Board board={board} onCellClick={placeShipIfNecessary}/></div>
            {options}
        </div>
    )
}

function ShipOptions({curOrientation, onShipClick, onOrientationChange} : {
    curOrientation: Orientation,
    onShipClick : (ship : string) => void,
    onOrientationChange : () => void
}) {
    function onChangeValue(event) {
        onShipClick(event.target.value)
    }
    return (
        <div>
            <div onChange={onChangeValue}>
                <label  className={styles.radLabel} >
                    <input type="radio" className={styles.radInput} value="CARRIER" name="gender"  />
                    <div className={styles.radDesign}></div>
                    <div className={styles.radText}>Carrier</div>
                </label>

                <label className={styles.radLabel}>
                    <input type="radio" className={styles.radInput} value="BATTLESHIP" name="gender"  />
                    <div className={styles.radDesign}></div>
                    <div className={styles.radText}>Battleship</div>
                </label>

                <label className={styles.radLabel}>
                    <input type="radio" className={styles.radInput} value="CRUISER" name="gender" />
                    <div className={styles.radDesign}></div>
                    <div className={styles.radText}>Cruiser</div>
                </label>

                <label className={styles.radLabel} >
                    <input type="radio" className={styles.radInput} value="SUBMARINE" name="gender" />
                    <div className={styles.radDesign}></div>
                    <div className={styles.radText}>Submarine</div>
                </label>
                <label className={styles.radLabel}>
                    <input type="radio" className={styles.radInput} value="DESTROYER" name="gender" />
                    <div className={styles.radDesign}></div>
                    <div className={styles.radText}>Destroyer</div>
                </label>
            </div>
            <label className={styles.switch}>
                <input type="checkbox" onClick={onOrientationChange}/>
                <span className={styles.slider}></span>
                <div id={styles.orientation} className={styles.radText}>{curOrientation}</div>
            </label>
        </div>
    )
}

function Battle({myBoard, enemyBoard, onShot} : {
    myBoard : Board, enemyBoard : Board, onShot : (x : number, y : number) => void
}) {
    return (
        <div className={styles.fullWidth}>
            <h1 className={styles.h1}>Battle Phase</h1>
            <div id={styles.myBoard}>
                <h2 className={styles.h2}>My Board</h2>
                <Board board={myBoard} onCellClick={() => {}}/>
            </div>
            <div id={styles.enemyBoard}>
                <h2 className={styles.h2}>Enemy Board</h2>
                <Board board={enemyBoard} onCellClick={onShot}/>
            </div>
        </div>
    )
}

// TODO: text appears to be invisible
function Finished({winner} : {winner : string}) {
    return (
        <div>
            <h1>Finished</h1>
            <p>{winner} has won</p>
        </div>
    )
}

function Board({board, onCellClick} : {board : Board, onCellClick? : (row: number, col: number) => void}) {
    const boardStr = board.cells
    const rowNumber = Math.sqrt(board.ncells)
    const collNumber = rowNumber
    return (
        <div id={styles.boardGame}>
            <table id={styles.gameTable}>
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
    const idNameCell = isHit ? styles.hit : isWater ? styles.water : styles.ship
    return (
        <button className={styles.cell} id={idNameCell} onClick={onClick}>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </button>
    )
}

function ErrorMsg({msg} : {msg : string}) {
    return (
        <div>
            <p style={{color: "red"}}>{msg}</p>
        </div>
    )
}