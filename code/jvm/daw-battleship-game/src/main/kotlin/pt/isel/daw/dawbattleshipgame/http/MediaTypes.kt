package pt.isel.daw.dawbattleshipgame.http

import org.springframework.http.MediaType

private const val APPLICATION_TYPE = "application"
private const val SIREN_SUBTYPE = "vnd.siren+json"

val SirenMediaType = MediaType.parseMediaType("$APPLICATION_TYPE/$SIREN_SUBTYPE")

val JsonMediaType = MediaType.APPLICATION_JSON