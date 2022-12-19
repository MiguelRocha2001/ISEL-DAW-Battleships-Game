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
let currentGameId: string | undefined
let userHomeLink: string | undefined
let placeShipsAction: Action | undefined
let confirmFleetAction: Action | undefined
let attackAction: Action | undefined


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
function setGetGameLink(link: string) {
    gameLink = link
}

function getCurrentGameIdLink() {
    return currentGameId
}
function setCurrentGameIdLink(id: string) {
    currentGameId = id
}

function getUserHomeLink() {
    return userHomeLink
}
function setUserHomeLink(link: string) {
    userHomeLink = link
}

function getPlaceShipsAction() {
    return placeShipsAction
}
function setPlaceShipsAction(action: Action) {
    placeShipsAction = action
}

function getConfirmFleetAction() {
    return confirmFleetAction
}

function setConfirmFleetAction(action: Action) {
    confirmFleetAction = action
}

function getAttackAction() {
    return attackAction
}
function setAttackAction(action: Action) {
    attackAction = action
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
    setGetGameLink,
    getCurrentGameIdLink,
    setCurrentGameIdLink,
    getUserHomeLink,
    setUserHomeLink,
    getPlaceShipsAction,
    setPlaceShipsAction,
    getConfirmFleetAction,
    setConfirmFleetAction,
    getAttackAction,
    setAttackAction,
}