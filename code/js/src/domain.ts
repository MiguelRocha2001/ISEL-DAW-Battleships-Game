export type Game = {
    id: number,
    configuration: GameConfiguration,
    player1: number,
    player2: number,
    state: string,
    board1: Board,
    board2: Board
}

type Board = {
    cells: string,
    nCells: number,
    isComplete: boolean
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
    nShotsPerRound: number,
    roundTimeout: number
}

export type CreateGameResponse = {
    gameState: string,
    gameId: number
}