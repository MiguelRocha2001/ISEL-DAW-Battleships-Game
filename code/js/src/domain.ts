export class Match {
    constructor(
        public id: number,
        public configuration: GameConfiguration,
        public player1: number,
        public player2: number,
        public state: string,
        public board1: Board,
        public board2: Board,
        public winner: number,
        public playerTurn: number,
        public localPlayer: string,
    ) {
        this.id = id;
        this.configuration = configuration;
        this.player1 = player1;
        this.player2 = player2;
        this.state = state;
        this.board1 = board1;
        this.board2 = board2;
        this.localPlayer = localPlayer;
        this.winner = winner;
        this.playerTurn = playerTurn;
    }
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