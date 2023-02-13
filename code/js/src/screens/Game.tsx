import * as React from 'react'
import {useEffect, useState} from 'react'
import {Services} from '../services'
import {Board, Fleet, GameConfiguration, isMyTurn, isTheSame, Match, Orientation} from '../domain'
import {Logger} from "tslog";
import styles from './Game.module.css'
import {Loading} from "./Loading";
import {ErrorScreen} from "./ErrorScreen";
import {ServerError} from "../utils/domain";


const logger = new Logger({ name: "GameComponent" });

type shot = {row: number, column: number}

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
        type : "checkingIfIsInWaitingQueue",
    }
    |
    {
        type : "menu",
        msg : string,
    }
    |
    {
        type : "creatingGame",
        config : GameConfiguration,
    }
    |
    {
        type : "playing",
        game: Match,
        msg : string
    }
    |
    {
        type : "placingShip",
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
        shots: shot[],
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
        type : "setCreatingGame",
        config : GameConfiguration,
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
        type : "setPlacingShip",
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
        shots: shot[],
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
    // logs the action
    if (action.type === "setError")
        logger.error("Error: " + action.error)
    else
        logger.info("Action: " + action.type)

    switch(action.type) {
        case "setError": {
            return { type: "error", error: action.error }
        }
        case "setCheckForExistingOnGoingGame" : {
            return {type: "checkingForExistingOnGoingGame"}
        }
        case 'setMenu' : {
            return {type : 'menu', msg: action.msg}
        }
        case 'setCreatingGame' : {
            return {type : 'creatingGame', config : action.config}
        }
        case 'setPlaying' : {
            return {type : 'playing', game : action.game, msg : null}
        }
        case 'setPlayingWithMsg' : {
            return {type : 'playing', game : action.game, msg : action.msg}
        }
        case 'setPlacingShip' : {
            return {type : 'placingShip', ship : action.ship, row : action.row, col : action.col, orient : action.orient}
        }
        case 'setConfirmingFleet' : {
            return {type : 'confirmingFleet'}
        }
        case 'setWaitingForConfirmation' : {
            return {type : 'waitingForConfirmation'}
        }
        case 'setShooting' : {
            return {type : 'shooting', shots : action.shots}
        }
        case 'setUpdatingGameWhileNecessary' : {
            return {type : 'updatingGameWhileNecessary', game : action.game, msg : action.msg}
        }
        case 'setQuitGame' : {
            return {type : 'quitGame', gameId : action.gameId}
        }
    }
}

export function Game() {
    const [state, dispatch] = React.useReducer(reducer, {type : 'checkingIfIsInWaitingQueue'})
    const [gameId, setGameId] = useState<number>(undefined)

    let cancelRequest = false

    async function checkIfIsInWaitingQueue() {
        logger.info("checkingIfIsInWaitingQueue")

        const result = await Services.isInGameQueue()
        if (cancelRequest) {
            logger.info("checkIfIsInWaitingQueue cancelled")
            return
        }
        if (result instanceof Error) {
            dispatchToErrorScreenOrDoHandler(result, () => {
                dispatch({type: 'setMenu', msg: result.message})
            })
        } else {
            if (result == true) {
                dispatch({type: 'setUpdatingGameWhileNecessary', game: undefined, msg : "You are in the waiting queue"})
            } else {
                dispatch({type: 'setCheckForExistingOnGoingGame'})
            }
        }
    }

    async function checkForExistingOnGoingGame() {
        logger.info("checkingForExistingOnGoingGame")
        const result = await Services.getCurrentActiveGame()
        if (cancelRequest) {
            logger.info("checkForExistingOnGoingGame cancelled")
            return
        }
        if (result instanceof Error) {
            dispatchToErrorScreenOrDoHandler(result, () => {
                dispatch({type: 'setMenu', msg: result.message})
            })
        } else {
            setGameId(result.id)
            dispatch({type:'setUpdatingGameWhileNecessary', game: result, msg : undefined})
        }
    }

    async function createGame(gameConfiguration: GameConfiguration) {
        logger.info("creatingGame")
        const result = await Services.createGame(gameConfiguration)
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
                dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: "Loading"})
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

    async function shoot(shots: shot[]) {

        async function dispatchOrDoNothing() : Promise<boolean> {
            if (gameId === undefined) {
                dispatch({type: 'setError', error: new Error("gameId should not be undefined")})
                return true
            }
            const result = await Services.getGame(gameId)

            if (result instanceof Error) {
                dispatch({type: 'setError', error: result})
                return true
            } else {
                // only dispatches when our shot was confirmed
                if (!isMyTurn(result)) {
                    dispatch({type:'setUpdatingGameWhileNecessary', game: result, msg: "Waiting for opponent's shot"})
                    return true
                } else
                    return false
            }
        }

        logger.info("shooting in: ", shots)
        const result = await Services.attack(
            {shots}
        )
        if (result instanceof Error) {
            dispatchToErrorScreenOrDoHandler(result, () => {
                dispatch({type:'setUpdatingGameWhileNecessary', game: undefined, msg: result.message})
            })
            return
        }

        const tic = setInterval(async () => {
            if (cancelRequest) {
                logger.info("shot cancelled")
                clearInterval(tic)
                return
            }
            if (await dispatchOrDoNothing()) {
                clearInterval(tic)
                return
            }
        }, 500)
    }

    async function updateUntilConfirmed() {
        async function dispatchOrDoNothing() : Promise<boolean> {
            if (gameId === undefined) {
                dispatch({type: 'setError', error: new Error("gameId should not be undefined")})
                return true
            }
            const result = await Services.getGame(gameId)

            if (result instanceof Error) {
                dispatch({type: 'setError', error: result})
                return true
            } else {
                const myBoard = result.localPlayer === 'one' ? result.board1 : result.board2
                if (myBoard.isConfirmed) {
                    dispatch({type: 'setUpdatingGameWhileNecessary', game: undefined, msg: 'Waiting for opponent to confirm'})
                    return true
                } else {
                    return false
                }
            }
        }

        logger.info("updatingUntilConfirmation")
        const tic = setInterval(async () => {
            if (cancelRequest) {
                logger.info("updateUntilConfirmation cancelled")
                clearInterval(tic)
                return
            }
            const dispatched = await dispatchOrDoNothing()
            if (dispatched) {
                clearInterval(tic)
                return
            }
        }, 500)
    }

    /**
     * Fetches game from data and updates state accordingly.
     */
    async function updateGameWhileNecessary() {

        /**
         * Checks if the appropriate msg is being displayed.
         * @param state the current state
         * @param msg the msg that should be displayed
         * @return true if state was changed and false otherwise
         */
        function updateStateToShowCorrectMsgIfNecessary(state: State, msg: string) {
            if (!cancelRequest && state.type === 'updatingGameWhileNecessary' && state.msg !== msg) {
                dispatch({type:'setUpdatingGameWhileNecessary', game: state.game, msg: msg})
                return true
            }
            return false
        }

        /**
         * Updates state to playing if is appropriate, and returns true if it was updated.
         * Otherwise, returns false.
         */
        async function dispatchToPlayingOrNothing(state: State) : Promise<boolean> {
            if (gameId === undefined) {
                const gameIdInternal = await Services.getCurrentGameId()
                if (typeof gameIdInternal === 'number') {
                    setGameId(gameIdInternal)
                }
            }

            if (gameId === undefined) return false

            const resp = await Services.getGame(gameId)

            if (resp instanceof Match) {
                if (!cancelRequest && state.type === 'updatingGameWhileNecessary' && !isTheSame(resp, state.game)) {
                    dispatch({type:'setUpdatingGameWhileNecessary', game: resp, msg: state.msg})
                    return true
                }

                switch (resp.state) {
                    case 'fleet_setup' : {
                        const player = resp.localPlayer
                        const myBoard = player === 'one' ? resp.board1 : resp.board2
                        const enemyBoard = player === 'one' ? resp.board2 : resp.board1
                        if (myBoard.isConfirmed && !enemyBoard.isConfirmed) { // awaiting opponent confirmation
                            return updateStateToShowCorrectMsgIfNecessary(state, 'Waiting for opponent to confirm')
                        }
                        if (!cancelRequest) {
                            dispatch({type: 'setPlaying', game: resp})
                            return true
                        } else
                            return false
                    }
                    case 'battle' : {
                        if (!cancelRequest && isMyTurn(resp)) {
                            dispatch({type: 'setPlaying', game: resp})
                            return true
                        } else
                            return updateStateToShowCorrectMsgIfNecessary(state, 'Waiting for opponent to shoot')
                    }
                    case 'finished' : {
                        if (!cancelRequest) {
                            dispatch({type: 'setPlaying', game: resp})
                            return true
                        }
                        return false
                    }
                }
            } else
                return false // no game available
        }

        logger.info("updatingGameWhileNecessary")

        const tid = setInterval(async () => {
            const dispatched = await dispatchToPlayingOrNothing(state)
            if (cancelRequest) {
                logger.info("updateGameWhileNecessary cancelled")
                clearInterval(tid)
                return
            }
            if (dispatched) {
                clearInterval(tid)
                return
            }
        }, 500)
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
                case 'checkingIfIsInWaitingQueue' : {
                    await checkIfIsInWaitingQueue()
                    break
                }
                case 'creatingGame' : {
                    await createGame(state.config)
                    break
                }
                case 'updatingGameWhileNecessary': {
                    await updateGameWhileNecessary()
                    break
                }
                case 'placingShip' : {
                    await placeShip(state.ship, state.row, state.col, state.orient)
                    break
                }
                case 'confirmingFleet' : {
                    await confirmFleet()
                    break
                }
                case 'waitingForConfirmation' : {
                    await updateUntilConfirmed()
                    break
                }
                case 'shooting' : {
                    await shoot(state.shots)
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
    }, [state, gameId])

    if (state.type === 'error') {
        return <ErrorScreen error={state.error}/>
    } else if (state.type === "checkingForExistingOnGoingGame") {
        return (<h1 id={styles.actionTitle}>Checking for existing on going game</h1>)
    } else if (state.type === "checkingIfIsInWaitingQueue") {
        return (<h1 id={styles.actionTitle}>Checking if is in waiting queue</h1>)
    } else if (state.type === "menu") {
        return <Menu onCreateGameRequest={(config: GameConfiguration) => dispatch({type : 'setCreatingGame', config})}/>
    } else if (state.type === "creatingGame") {
        return <CreatingGame />
    } else if (state.type === "playing") {
        return <Playing
            match={state.game}
            onPlaceShip={(ship, row, col, orient) => dispatch({type : 'setPlacingShip', ship, row, col, orient})}
            onConfirmFleetRequest={() => dispatch({type : 'setConfirmingFleet'})}
            onShot={(shots: shot[]) => dispatch({type : 'setShooting', shots})}
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
    } else if (state.type === "placingShip") {
        return (<h1 id={styles.actionTitle}>Placing Ship</h1>)
    } else if (state.type === "confirmingFleet") {
        return (<h1 id={styles.actionTitle}>Confirming fleet</h1>)
    } else if (state.type === "shooting") {
        return (<h1 id={styles.actionTitle}>Making the Shot</h1>)
    } else if (state.type === "quitGame") {
        return (<h1 id={styles.actionTitle}>Quiting Game</h1>)
    } else {
        return (<h1 id={styles.actionTitle}>Unknown state</h1>)
    }
}

function Menu({onCreateGameRequest} : { onCreateGameRequest: (conf: GameConfiguration) => void }) {
    const [
        onBoardSizeChange,
        onNShotsPerRoundChange,
        onRoundTimeoutChange,
        onCarrierChange,
        onBattleshipChange,
        onCruiserChange,
        onSubmarineChange,
        onDestroyerChange,
        gameConfiguration,
    ] = useGameConfiguration()

    return (
        <div id={styles.menu}>
        <div id = {styles.buttonsToPlay}>
            <div id={styles.inputWrapper}>
                <label htmlFor="quantity">Board size</label>
                <input type="number" id="quantity" name="quantity" min="8" max="13" value={gameConfiguration.boardSize} onChange={onBoardSizeChange} />
            </div>

            <div id={styles.inputWrapper}>
                <label htmlFor="quantity">Shots per round</label>
                <input type="number" id="quantity" name="quantity" min="1" max="5" value={gameConfiguration.nshotsPerRound} onChange={onNShotsPerRoundChange}/>
            </div>

            <div id={styles.inputWrapper}>
                <label htmlFor="quantity">Round timeout</label>
                <input type="number" id="quantity" name="quantity" min="10" max="240" value={gameConfiguration.roundTimeout} onChange={onRoundTimeoutChange}/>
            </div>

            <div id={styles.inputWrapper}>
                <label htmlFor="quantity">Carrier size</label>
                <input type="number" id="quantity" name="quantity" min="1" max="5" value={gameConfiguration.fleet.CARRIER} onChange={onCarrierChange}/>
            </div>

            <div id={styles.inputWrapper}>
                <label htmlFor="quantity">Battleship Size</label>
                <input type="number" id="quantity" name="quantity" min="1" max="5" value={gameConfiguration.fleet.BATTLESHIP} onChange={onBattleshipChange}/>
            </div>

            <div id={styles.inputWrapper}>
                <label htmlFor="quantity">Cruiser Size</label>
                <input type="number" id="quantity" name="quantity" min="1" max="5" value={gameConfiguration.fleet.CRUISER} onChange={onCruiserChange}/>
            </div>

            <div id={styles.inputWrapper}>
                <label htmlFor="quantity">Submarine Size</label>
                <input type="number" id="quantity" name="quantity" min="1" max="5" value={gameConfiguration.fleet.SUBMARINE} onChange={onSubmarineChange}/>
            </div>

            <div id={styles.inputWrapper}>
                <label htmlFor="quantity">Destroyer Size</label>
                <input type="number" id="quantity" name="quantity" min="1" max="5" value={gameConfiguration.fleet.DESTROYER} onChange={onDestroyerChange}/>
            </div>
        </div>
            <button id={styles.newGame} className={styles.cybrBtn} onClick={() => {
                onCreateGameRequest(gameConfiguration)
            }}>
                Create New<span aria-hidden></span>
                <span aria-hidden className={styles.cybrbtn__glitch}>Create New</span>
            </button>
        </div>
    )
}

function useGameConfiguration(): Array<any> {
    const [boardSize, setBoardSize] = useState(10)
    const [nShotsPerRound, setNShotsPerRound] = useState(1)
    const [roundTimeout, setRoundTimeout] = useState(10)
    const [carrierSize, setCarrierSize] = useState(5)
    const [battleshipSize, setBattleshipSize] = useState(4)
    const [cruiserSize, setCruiserSize] = useState(3)
    const [submarineSize, setSubmarineSize] = useState(3)
    const [destroyerSize, setDestroyerSize] = useState(2)

    const fleet = {
        CARRIER : carrierSize,
        BATTLESHIP : battleshipSize,
        CRUISER : cruiserSize,
        SUBMARINE : submarineSize,
        DESTROYER : destroyerSize
    }

    function onBoardSizeChange(event: React.ChangeEvent<HTMLInputElement>) {
        const value = parseInt(event.target.value)
        if (!isNaN(value) && value >= 8 && value <= 13)
            setBoardSize(parseInt(event.target.value))
    }
    function onNShotsPerRoundChange(event: React.ChangeEvent<HTMLInputElement>) {
        const value = parseInt(event.target.value)
        if (!isNaN(value) && value >= 1 && value <= 5)
            setNShotsPerRound(parseInt(event.target.value))
    }
    function onRoundTimeoutChange(event: React.ChangeEvent<HTMLInputElement>) {
        const value = parseInt(event.target.value)
        if (!isNaN(value) && value >= 10 && value <= 240)
            setRoundTimeout(parseInt(event.target.value))
    }

    function isValidShipSize(size: number) { return !isNaN(size) && size >= 1 && size <= 5 }

    function onCarrierSizeChange(event: React.ChangeEvent<HTMLInputElement>) {
        const value = parseInt(event.target.value)
        if (isValidShipSize(value))
            setCarrierSize(parseInt(event.target.value))
    }
    function onBattleshipSizeChange(event: React.ChangeEvent<HTMLInputElement>) {
        const value = parseInt(event.target.value)
        if (isValidShipSize(value))
            setBattleshipSize(parseInt(event.target.value))
    }
    function onCruiserSizeChange(event: React.ChangeEvent<HTMLInputElement>) {
        const value = parseInt(event.target.value)
        if (isValidShipSize(value))
            setCruiserSize(parseInt(event.target.value))
    }
    function onSubmarineSizeChange(event: React.ChangeEvent<HTMLInputElement>) {
        const value = parseInt(event.target.value)
        if (isValidShipSize(value))
            setSubmarineSize(parseInt(event.target.value))
    }
    function onDestroyerSizeChange(event: React.ChangeEvent<HTMLInputElement>) {
        const value = parseInt(event.target.value)
        if (isValidShipSize(value))
            setDestroyerSize(parseInt(event.target.value))
    }

    return [
        onBoardSizeChange,
        onNShotsPerRoundChange,
        onRoundTimeoutChange,
        onCarrierSizeChange,
        onBattleshipSizeChange,
        onCruiserSizeChange,
        onSubmarineSizeChange,
        onDestroyerSizeChange,
        {boardSize, nshotsPerRound: nShotsPerRound, roundTimeout, fleet}
    ]
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
    onShot : (shots: shot[]) => void,
    onQuitRequest : (gameId: number) => void }) {

    function onConfirmFleet() {
        onConfirmFleetRequest()
    }

    let isPlayerOne = match.localPlayer === "one"
    let myBoard: Board = isPlayerOne ? match.board1 : match.board2

    let fleetConfirmed: boolean = isPlayerOne ? match.board1.isConfirmed : match.board2.isConfirmed
    let enemyBoard: Board = isPlayerOne ? match.board2 : match.board1
    let winner: string | undefined = match.winner == match.player1 && isPlayerOne ? "You" : "Opponent"

    const quitButton = <button  id={styles.quitButton} className={styles.cybrBtn} onClick={() => {onQuitRequest(match.id)}}>
        Leave Match<span aria-hidden></span>
        <span aria-hidden className={styles.cybrbtn__glitch}>Leave Match</span>
    </button>

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
                        buttonQuit = {quitButton}
                    />

                </div>
            )
        case "battle":
            return (
                <div>
                    <Battle myBoard={myBoard} enemyBoard={enemyBoard} isMyTurn={isMyTurn(match)} nShotsPerRound={match.configuration.nshotsPerRound} onShot={onShot}/>
                    {quitButton}
                </div>
            )
        case "finished":
            return (
                <Finished winner={winner} />
            )
    }
}

function FleetSetup({board, fleetConfirmed, onPlaceShip, onConfirmFleet, config, buttonQuit}: {
    board : Board,
    fleetConfirmed : boolean,
    onPlaceShip : (ship: string, x : number, y : number, o: Orientation) => void,
    onConfirmFleet : () => void,
    config : GameConfiguration,
    buttonQuit : JSX.Element
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

    const title = fleetConfirmed == true ? "Wait For Opponent" : "Place your ships"

    const options = fleetConfirmed == false ? (
        <div className={styles.right}>
            <button className={styles.cybrBtn} onClick={onConfirmFleetAux}>
                Ready!<span aria-hidden>_</span>
                <span aria-hidden className={styles.cybrbtn__glitch}>Ready</span>
            </button>
            {buttonQuit}
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

function Battle({myBoard, enemyBoard, isMyTurn, nShotsPerRound, onShot} : {
    myBoard : Board,
    enemyBoard : Board,
    isMyTurn : boolean,
    nShotsPerRound: number,
    onShot : (shots: shot[]) => void,
}) {
    const [shots, setShots] = useState<shot[]>([])
    function onShotHandler(row: number, column: number) {
        if (shots.length < nShotsPerRound) {
            setShots(shots.concat([{row, column}]))
        }
        if (shots.length === nShotsPerRound - 1) {
            const totalShots = shots.concat([{row, column}])
            onShot(totalShots)
            setShots([])
        }
    }

    const shotsLeft = isMyTurn ? nShotsPerRound - shots.length : 0

    return (
        <div className={styles.fullWidth}>
            <h1 className={styles.h1}>Battle Phase</h1>
            <h3>Shots left: {shotsLeft}</h3>
            <div id={styles.myBoard}>
                <h2 className={styles.h2}>My Board</h2>
                <Board board={myBoard} onCellClick={() => {}}/>
            </div>
            <div id={styles.enemyBoard}>
                <h2 className={styles.h2}>Enemy Board</h2>
                <Board board={enemyBoard} onCellClick={onShotHandler}/>
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