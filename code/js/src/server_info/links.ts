import * as React from 'react'
import {
    useState,
    createContext,
    useContext,
} from 'react'
import { Action } from "../utils/siren"

const host = 'http://localhost:8080/api'

const defaultUrl = '/'

let infoLink: string | undefined
let battleshipRanksLink: string | undefined
let tokenAction: Action | undefined
let registerAction: Action | undefined
let createGameAction: Action | undefined
let gameLink: string | undefined


function getInfoLink() {
    return infoLink
}
function setInfoLink(link: string) {
    infoLink = link
}

function getBattleshipRanksLink() {
    return battleshipRanksLink
}
function setBattleshipRanksLink(link: string) {
    battleshipRanksLink = link
}

function getTokenAction() {
    return tokenAction
}
function setTokenAction(action: Action) {
    tokenAction = action
}

function getRegisterAction() {
    return registerAction
}
function setRegisterAction(action: Action) {
    registerAction = action
}

function getCreateGameAction() {
    return createGameAction
}
function setCreateGameAction(action: Action) {
    createGameAction = action
}

function getGameLink() {
    return gameLink
}
function setGameLink(link: string) {
    gameLink = link
}

export const links = {
    host,
    defaultUrl,
    getInfoLink,
    setInfoLink,
    getBattleshipRanksLink,
    setBattleshipRanksLink,
    getTokenAction,
    setTokenAction,
    getRegisterAction,
    setRegisterAction,
    getCreateGameAction,
    setCreateGameAction,
    getGameLink,
    setGameLink,
}