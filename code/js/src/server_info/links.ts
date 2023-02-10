import {Action} from "../utils/siren"

const host = 'http://localhost:8080/api'

const defaultUrl = '/'

let infoLink: string | undefined
let battleshipRanksLink: string | undefined
let userLink: string | undefined
let tokenAction: Action | undefined
let registerAction: Action | undefined
let createGameAction: Action | undefined
let gameLink: string | undefined
let currentGameId: string | undefined
let userHomeLink: string | undefined
let placeShipsAction: Action | undefined
let confirmFleetAction: Action | undefined
let attackAction: Action | undefined
let quitGameAction: Action | undefined
let isInGameQueueLink: string | undefined
let quitGameQueueAction: Action | undefined

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

function getUserLink() {
    return userLink
}
function setUserLink(link: string) {
    userLink = link
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

function getQuitGameAction() {
    return quitGameAction
}
function setQuitGameAction(action: Action) {
    quitGameAction = action
}

function getIsInGameQueueLink() {
    return isInGameQueueLink
}
function setIsInGameQueueLink(link: string) {
    isInGameQueueLink = link
}

function getQuitGameQueueAction() {
    return quitGameQueueAction
}
function setQuitGameQueueAction(action: Action) {
    quitGameQueueAction = action
}

export const links = {
    host,
    defaultUrl,
    getInfoLink,
    setInfoLink,
    getBattleshipRanksLink,
    setBattleshipRanksLink,
    getUserLink,
    setUserLink,
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
    getQuitGameAction,
    setQuitGameAction,
    getIsInGameQueueLink,
    setIsInGameQueueLink,
    getQuitGameQueueAction,
    setQuitGameQueueAction,
}