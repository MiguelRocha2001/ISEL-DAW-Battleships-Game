export type Game = {
    id: number,
    configuration: GameConfiguration,
    player1: number,
    player2: number,
    state: string,
    board1: Board,
    board2: Board
}

export type Board = {
    cells: string,
    ncells: number,
    isConfirmed: boolean
}

type GameConfiguration = {
    boardSize: number,
    fleet: any,
    nShotsPerRound: number
    roundTimeout: number
}

export type CreateGameRequest = {
    boardSize: number,
    fleet: any,
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
    orientation: string
}

type Position = {
    row: number,
    column: number
}