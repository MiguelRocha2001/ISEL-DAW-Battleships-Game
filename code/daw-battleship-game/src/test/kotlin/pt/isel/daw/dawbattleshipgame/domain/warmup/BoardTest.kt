package pt.isel.daw.dawbattleshipgame.domain.warmup

import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import org.junit.jupiter.api.Assertions.assertEquals
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

class BoardTest {
    @Test
    fun test_dbString_1(){
        val sut = Board(10).getDbString()
        val expected =
           //ABCDEFGHIJ
       /*1*/"          " +
       /*2*/"          " +
       /*3*/"          " +
       /*4*/"          " +
       /*5*/"          " +
       /*6*/"          " +
       /*7*/"          " +
       /*8*/"          " +
       /*9*/"          " +
      /*10*/"          "
        assertEquals(expected, sut)
    }

    @Test
    fun test_dbString_placeship(){
        var sut = Board(10)
        var expected =
                //ABCDEFGHIJ
            /*1*/"          " +
            /*2*/"          " +
            /*3*/"          " +
            /*4*/"          " +
            /*5*/"          " +
            /*6*/"          " +
            /*7*/"          " +
            /*8*/"          " +
            /*9*/"          " +
           /*10*/"          "
        assertEquals(expected, sut.getDbString())

        sut = sut.placeShip(
            setOf(
                "A1".toCoordinate(),
                "B1".toCoordinate()
            ),
            ShipType.CARRIER
        )

        expected =
                     //ABCDEFGHIJ
                /*1*/"CC        " +
                /*2*/"          " +
                /*3*/"          " +
                /*4*/"          " +
                /*5*/"          " +
                /*6*/"          " +
                /*7*/"          " +
                /*8*/"          " +
                /*9*/"          " +
                /*10*/"          "

        assertEquals(expected, sut.getDbString())

    }

    @Test
    fun test_dbString_placeship_and_placeshot(){
        var sut = Board(10)
        var expected =
                    //ABCDEFGHIJ
                /*1*/"          " +
                /*2*/"          " +
                /*3*/"          " +
                /*4*/"          " +
                /*5*/"          " +
                /*6*/"          " +
                /*7*/"          " +
                /*8*/"          " +
                /*9*/"          " +
                /*10*/"          "
        assertEquals(expected, sut.getDbString())

        sut = sut.placeShip(
            setOf(
                "A1".toCoordinate(),
                "B1".toCoordinate()
            ),
            ShipType.CARRIER
        )
        sut = sut.placeShot("A1".toCoordinate())

        expected =
                //ABCDEFGHIJ
                /*1*/"cC        " +
                /*2*/"          " +
                /*3*/"          " +
                /*4*/"          " +
                /*5*/"          " +
                /*6*/"          " +
                /*7*/"          " +
                /*8*/"          " +
                /*9*/"          " +
                /*10*/"          "

        assertEquals(expected, sut.getDbString())

    }

    @Test
    fun test_create_Board_from_string(){
        val expected =
                    //ABCDEFGHIJ
                /*1*/"cC        " +
                /*2*/"          " +
                /*3*/"          " +
                /*4*/"          " +
                /*5*/"          " +
                /*6*/"          " +
                /*7*/"          " +
                /*8*/"          " +
                /*9*/"          " +
                /*10*/"          "

        val sut = Board(expected)

        assertEquals(expected, sut.getDbString())
        assertEquals(true, sut[0].isHit)
        assertEquals(false, sut[1].isHit)

    }



}