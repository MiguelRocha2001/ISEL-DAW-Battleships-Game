package pt.isel.daw.dawbattleshipgame.domain.board

import pt.isel.daw.dawbattleshipgame.domain.ship.ShipSet
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.ship.generateShip
import pt.isel.daw.dawbattleshipgame.domain.ship.types.*

class Board {
    val board: List<Panel>
    private val _coordinates: Coordinates
    val coordinates: List<Coordinate>
        get() = _coordinates.values()
    private val dimension: Int

    /**
     * Initiates the board with empty panels (Water Panels)
     */
    constructor(dim: Int) {
        _coordinates = Coordinates(dim)
        dimension = dim
        board = List(dim * dim) { WaterPanel(false) }
    }

    /**
     * Use this constructor to restore the board.
     * @param positions map with coordinates and correspondent panels
     */
    constructor(old: Board, positions: Map<Coordinate, Panel>) {
        this._coordinates = old._coordinates
        this.dimension = old.dimension
        board = mutableListOf()
        positions.forEach { (c, p) ->
            board[getIdx(c)] = p
        }
    }

    /**
     * Creates a new Board, adding ShipPanel (non hit) to given [coordinateSet].
     */
    constructor(old: Board, coordinateSet: CoordinateSet, shipType: ShipType) {
        this._coordinates = old._coordinates
        this.dimension = old.dimension
        var newBoard = old.board

        coordinateSet.forEach {
            newBoard = newBoard.placePanel(it, ShipPanel(shipType))
        }
        board = newBoard
    }

    /**
     * Creates a new Board, adding WaterPanel (non hit) to given [coordinateSet].
     */
    constructor(old: Board, coordinateSet: CoordinateSet) {
        this._coordinates = old._coordinates
        this.dimension = old.dimension
        var newBoard = old.board

        val waterPanel = WaterPanel(false)
        coordinateSet.forEach {
            newBoard = newBoard.placePanel(it, waterPanel)
        }
        board = newBoard
    }

    /**
     * Retrieves a new Board with the panel at the given coordinate changed.
     */
    constructor(old: Board, coordinate: Coordinate, panel: Panel) {
        this._coordinates = old._coordinates
        this.dimension = old.dimension
        board = old.board.placePanel(coordinate, panel)
    }

    internal operator fun get(coordinate: Coordinate): Panel {
        return board[coordinate]
    }

    private operator fun List<Panel>.get(c: Coordinate): Panel {
        return board[getIdx(c)]
    }

    private fun getIdx(c: Coordinate): Int {
        return (c.row - 1) * dimension + (c.column - 1)
    }

    override fun toString(): String {
        var str = "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n"
        for (i in 0 until dimension) {
            str += "| ${i+1}"
            str = if (i+1 < 10 ) "$str |" else "$str|"
            for (j in 0 until dimension) {
                str += ' ' + board[i * dimension + j].toString() + " |"
            }
            str += "\n"
        }
        return str
    }

    private fun List<Panel>.placePanel(c: Coordinate, panel: Panel): List<Panel> {
        val newBoardList = this.toMutableList()
        newBoardList[(getIdx(c))] = panel
        return newBoardList
    }

    /**
     * Places a ShipPanel (non hit) on the Board.
     * @return newly created Board, with [cs] set.
     */
    fun placeShipPanel(cs: CoordinateSet, shipType: ShipType) =
        Board(this, cs, shipType)

    /**
     * Places a WaterPanel (non hit) on the Board.
     */
    fun placeWaterPanel(cs: CoordinateSet) =
        Board(this, cs)

    fun isShipPanel(c: Coordinate) = board[c] is ShipPanel

    fun isWaterPanel(c: Coordinate) = board[c] is WaterPanel

    /**
     * Places a shot on given coordinate.
     */
    fun hitPanel(c: Coordinate): Board? {
        val localPanel = board[c]
        if (localPanel.isHit) return null
        val newPanel = localPanel.getPanelHit()
        return Board(this, c, newPanel)
    }

    fun getHitCoordinates() = _coordinates.values().filter { c -> board[c].isHit }

    fun getShipCoordinates() = _coordinates.values().filter { c -> board[c] is ShipPanel }

    /**
     * Gets all ships from board
     */
    fun getShips(): ShipSet {
        val shipSet = mutableSetOf<Ship>()
        ShipType.values().forEach {
            val s = getShipFromBoard(it)
            if(s != null) shipSet.add(s)
        }
        return shipSet
    }


    /**
     * Returns a ship from the board, given a specific ShipType
     */
    private fun getShipFromBoard(type : ShipType): Ship? {
        val coordinates =
            coordinates.map { it to board[it] }
                .filter {
                    it.second is ShipPanel &&
                            (it.second as ShipPanel).shipType == type
                }
                .map { it.first }
                .toSet()
        if (coordinates.isEmpty()) return null
        return type.generateShip(coordinates)
    }
}