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

export function isTheSame(a: Match, b: Match): boolean {
    if (!a || !b) return false;
    const res1 = a.id === b.id &&
        a.player1 === b.player1 &&
        a.player2 === b.player2 &&
        a.state === b.state &&
        a.winner === b.winner &&
        a.playerTurn === b.playerTurn &&
        a.localPlayer === b.localPlayer;

    const res2 = isTheSameBoard(a.board1, b.board1)
        && isTheSameBoard(a.board2, b.board2);

    const res3 = isTheSameGameConfiguration(a.configuration, b.configuration);

    return res1 && res2 && res3;
}

export function isMyTurn(game: Match) {
    const myPlayer = game.localPlayer
    const playerTurn = game.playerTurn
    if (playerTurn === game.player1 && myPlayer === 'one') return true
    return playerTurn === game.player2 && myPlayer === 'two';
}

export type Board = {
    cells: string,
    ncells: number,
    isConfirmed: boolean
}

export function isTheSameBoard(a: Board, b: Board): boolean {
    return a.ncells === b.ncells &&
        a.isConfirmed === b.isConfirmed &&
        a.cells === b.cells;
}

export type GameConfiguration = {
    boardSize: number,
    fleet: Fleet,
    nshotsPerRound: number
    roundTimeout: number
}

export function isTheSameGameConfiguration(a: GameConfiguration, b: GameConfiguration): boolean {
    return a.boardSize === b.boardSize &&
        a.nshotsPerRound === b.nshotsPerRound &&
        a.roundTimeout === b.roundTimeout &&
        isTheSameFleet(a.fleet, b.fleet);
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

export function isTheSameFleet(a: Fleet, b: Fleet): boolean {
    return a.CARRIER === b.CARRIER &&
        a.BATTLESHIP === b.BATTLESHIP &&
        a.CRUISER === b.CRUISER &&
        a.SUBMARINE === b.SUBMARINE &&
        a.DESTROYER === b.DESTROYER;
}

export type Position = {
    row: number,
    column: number
}