import * as React from 'react'
import {useEffect, useState} from 'react'
import {InvalidArgumentError, ResolutionLinkError, Services} from '../services'
import {Board, Fleet, GameConfiguration, Match, Orientation} from '../domain'
import {Logger} from "tslog";
import styles from './Game.module.css'
import {useCurrentUser} from "./auth/Authn";
import {Loading} from "./Loading";
import {Button} from "react-bootstrap";
import {ErrorScreen} from "./ErrorScreen";
import {NetworkError, ServerError} from "../utils/domain";


const logger = new Logger({ name: "GameComponent" });

type State =
    {
        type: "error",
        error: Error
    }
    |
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
        game: Match,
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
        game : Match,
        msg : string
    }
    |
    {
        type : "quitGame",
        gameId : number
    }

type Action =
    {
        type : "setError",
        error : Error
    }
    |
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
        game : Match
    }
    |
    {
        type : "setPlayingWithMsg",
        game : Match,
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
        game : Match,
        msg : string
    }
    |
    {
        type : "setQuitGame",
        gameId : number
    }

function reducer(state: State, action: Action): State {
    switch(action.type) {
        case "setError": {
            logger.error("Error: " + action.error)
            return { type: "error", error: action.error }
        }
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
    let cancelRequest = false

    async function checkForExistingOnGoingGame() {
        logger.info("checkingForExistingOnGoingGame")
        const result = await Services.getGame()
        if (cancelRequest) {
            logger.info("checkForExistingOnGoingGame cancelled")
            return
        }
        if (result instanceof Error) {
            dispatchToErrorScreenOrDoHandler(result, () => {
                dispatch({type: 'setMenu', msg: result.message})
            })
        } else {
            dispatch({type:'setUpdatingGameWhileNecessary', game: result, msg : undefined})
        }
    }

    async function createGame() {
        logger.info("creatingGame")
        const result = await Services.createGame({
            boardSize: 10,
            fleet: {
                "CARRIER": 5,
                "BATTLESHIP": 4,
                "CRUISER": 3,
                "SUBMARINE": 3,
                "DESTROYER": 2
            },
            shots: 1,
            roundTimeout: 10,
        })
        if (cancelRequest) {
            logger.info("createGame cancelled")
            return
        }
        if (result instanceof Error) {
            dispatchToErrorScreenOrDoHandler(result, () => {
                if (result.message === "User already in a queue")
                    dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg : 'Matchmaking'})
                else
                    dispatch({type: 'setMenu', msg: result.message})
            })
        } else dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg : 'Matchmaking'})
    }


    async function placeShip(ship: string, row: number, col: number, orientation: Orientation) {
        logger.info("placingShip: " + ship + " at " + row + "," + col + " " + orientation)
        if (ship) {
            const result = await Services.placeShips({
                operation: "place-ships",
                ships: [
                    {
                        shipType: ship,
                        position: {row: row, column: col},
                        orientation: orientation
                    }
                ],
                fleetConfirmed: false
            })
            if (cancelRequest) {
                logger.info("placeShip cancelled")
                return
            }
            if (result instanceof Error) {
                dispatchToErrorScreenOrDoHandler(result, () => {
                    dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: result.message})
                })
            } else {
                dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: undefined})
            }
        }
    }

    async function confirmFleet() {
        logger.info("confirming fleet")
        const result = await Services.confirmFleet()
        if (cancelRequest) {
            logger.info("confirmFleet cancelled")
            return
        }
        if (result instanceof Error) {
            dispatchToErrorScreenOrDoHandler(result, () => {
                dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: result.message})
            })
        } else {
            dispatch({type:'setWaitingForConfirmation'})
        }
    }

    async function shoot(row: number, col: number) {
        logger.info("shooting in " + row + " " + col)
        const result = await Services.attack(
            {shots: Array({row: row, column: col})}
        )
        if (cancelRequest) {
            logger.info("shoot cancelled")
            return
        }
        if (result instanceof Error) {
            dispatchToErrorScreenOrDoHandler(result, () => {
                dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: result.message})
            })
        } else {
            dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: undefined})
        }
    }

    async function updateUntilConfirmation() {
        logger.info("updatingUntilConfirmation")
        const result = await Services.getGame()
        if (cancelRequest) {
            logger.info("updateUntilConfirmation cancelled")
            return
        }
        if (result instanceof Error) {
            dispatchToErrorScreenOrDoHandler(result, () => {
                dispatch({type:'setWaitingForConfirmation'})
            })
        } else {
            const myBoard = result.myPlayer === 'one' ? result.board1 : result.board2
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
        function isMyTurn(game: Match) {
            const myPlayer = game.myPlayer
            const playerTurn = game.playerTurn
                if (playerTurn === game.player1 && myPlayer === 'one') return true
            return playerTurn === game.player2 && myPlayer === 'two';
        }

        logger.info("updateGameWhileNecessary")
        const resp = await Services.getGame()
        if (cancelRequest) {
            logger.info("updateGameWhileNecessary cancelled")
            return
        }
        if (resp instanceof Match) {
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
        const result = await Services.quitGame(gameId)
        if (cancelRequest) {
            logger.info("quitGame cancelled")
            return
        }
        if (result instanceof Error) {
            dispatchToErrorScreenOrDoHandler(result, () => {
                dispatch({type:'setMenu', msg: result.message})
            })
        } else {
            dispatch({type:'setMenu', msg: undefined})
        }
    }

    function dispatchToErrorScreenOrDoHandler(error: Error, handler: () => void) {
        if (error instanceof ServerError)
            handler()
        else
            dispatch({type: 'setError', error: error})
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

    if (state.type === 'error') {
        return <ErrorScreen error={state.error}/>
    } else if (state.type === "checkingForExistingOnGoingGame") {
        return <CheckingForExistingOnGoingGame />
    } if (state.type === "menu") {
        return <Menu onCreateGameRequest={() => dispatch({type : 'setCreatingGame'})}/>
    } else if (state.type === "creatingGame") {
        return <CreatingGame />
    } else if (state.type === "playing") {
        return <Playing
            match={state.game}
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
                        match={state.game}
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

function Playing({match, onPlaceShip, onConfirmFleetRequest, onShot, onQuitRequest} : {
    match : Match,
    onPlaceShip : (ship: string, x : number, y : number, o: Orientation) => void,
    onConfirmFleetRequest : () => void,
    onShot : (x : number, y : number) => void,
    onQuitRequest : (gameId: number) => void }) {

    function onConfirmFleet() {
        onConfirmFleetRequest()
    }

    let isPlayerOne = match.myPlayer === "one"
    let myBoard: Board = isPlayerOne ? match.board1 : match.board2

    /*
    console.log("-------------MYBOARD--------------")
    console.log(myBoard)
    console.log(match.board1)
     */

    let fleetConfirmed: boolean = isPlayerOne ? match.board1.isConfirmed : match.board2.isConfirmed
    let enemyBoard: Board = isPlayerOne ? match.board2 : match.board1
    let winner: string | undefined = match.winner == match.player1 && isPlayerOne ? "You" : "Opponent"

    const quitButton = <Button onClick={() => {onQuitRequest(match.id)}} variant="contained" color="secondary">Quit</Button>

    switch(match.state) {
        case "fleet_setup":
            return (
                <div>
                    <FleetSetup
                        board={myBoard}
                        fleetConfirmed={fleetConfirmed}
                        onPlaceShip={(ship, x, y, o) => onPlaceShip(ship, x, y, o)}
                        onConfirmFleet={onConfirmFleet}
                        config={match.configuration}
                    />
                    {quitButton}
                </div>
            )
        case "battle":
            return (
                <div>
                    <Battle myBoard={myBoard} enemyBoard={enemyBoard} onShot={onShot}/>
                    {quitButton}
                </div>
            )
        case "finished":
            return (
                <Finished winner={winner} />
            )
    }
}

function FleetSetup({board, fleetConfirmed, onPlaceShip, onConfirmFleet, config}: {
    board : Board,
    fleetConfirmed : boolean,
    onPlaceShip : (ship: string, x : number, y : number, o: Orientation) => void,
    onConfirmFleet : () => void,
    config : GameConfiguration
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
                    ships={config.fleet}
                />
            </div>
        </div>
    ) : null
    return (
        <div className={styles.fullWidth}>
            <h1 className={styles.h1}>{title}</h1>
            <div className={styles.left}>
                <Board board={board} onCellClick={placeShipIfNecessary}/></div>
            {options}
        </div>
    )
}

function ShipOptions({curOrientation, onShipClick, onOrientationChange, ships} : {
    curOrientation: Orientation,
    onShipClick : (ship : string) => void,
    onOrientationChange : () => void,
    ships: Fleet
}) {
    function onChangeValue(event) {
        onShipClick(event.target.value)
    }

    let shipsJsxList = []

    // console.log(ships)

    for (let key in ships) {
        shipsJsxList.push(
            <li key={key} style={{display: "block"}}>
                <ShipLabel shipType={key}/>
            </li>
        )
    }

    return (
        <div>
            <div onChange={onChangeValue}>
                {shipsJsxList}
            </div>
            <label className={styles.switch}>
                <input type="checkbox" onClick={onOrientationChange}/>
                <span className={styles.slider}></span>
                <div id={styles.orientation} className={styles.radText}>{curOrientation}</div>
            </label>
        </div>
    )
}

function ShipLabel({shipType} : {shipType : string}) {
    return <label  className={styles.radLabel} >
        <input type="radio" className={styles.radInput} value={shipType} name="gender"  />
        <div className={styles.radDesign}></div>
        <div className={styles.radText}>{shipType.toString()}</div>
    </label>
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