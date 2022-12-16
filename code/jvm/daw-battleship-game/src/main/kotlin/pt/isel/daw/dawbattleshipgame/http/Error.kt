package pt.isel.daw.dawbattleshipgame.http

import pt.isel.daw.dawbattleshipgame.http.model.Problem
import java.net.URI

class ApiException(
        val type : URI,
        val title : String,
        val detail : String
        ) : Exception(detail)

fun requireWithException(title: String, detail: String,
                         type: URI = Problem.DEFAULT_URI, f: () -> Boolean
) = if(!f()) throw ApiException(type, title, detail)
    else Unit