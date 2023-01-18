export type Game = {
    id: number,
    configuration: GameConfiguration,
    player1: number,
    player2: number,
    state: string,
    board1: Board,
    board2: Board,
    myPlayer: string,
    winner: number,
    playerTurn: number,
}

export type Board = {
    cells: string,
    ncells: number,
    isConfirmed: boolean
}

export type GameConfiguration = {
    boardSize: number,
    fleet: Fleet,
    nShotsPerRound: number
    roundTimeout: number
}

export type CreateGameRequest = {
    boardSize: number,
    fleet: Fleet,
    shots: number,
    roundTimeout: number
}

export type CreateGameResponse = {
    gameState: string,
    gameId: number
}

export type PlaceShipsRequest = {
    operation: string,
    ships: PlaceShipRequest[]
    fleetConfirmed: boolean
}

type PlaceShipRequest = {
    shipType: string,
    position: Position,
    orientation: Orientation
}

export type Orientation = 'HORIZONTAL' | 'VERTICAL'

export type Fleet = {
    CARRIER? : number,
    BATTLESHIP? : number,
    CRUISER? : number,
    SUBMARINE? : number,
    DESTROYER? : number
}

export type Position = {
    row: number,
    column: number
}