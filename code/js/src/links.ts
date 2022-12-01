import { Action } from "./utils/siren"

const host = 'http://localhost:8080/api'

const defaultUrl = host

let infoLink: string | undefined
let battleshipRanksLink: string | undefined
let tokenAction: Action | undefined

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

export const links = {
    host,
    defaultUrl,
    getInfoLink,
    setInfoLink,
    getBattleshipRanksLink,
    setBattleshipRanksLink,
    getTokenAction,
    setTokenAction,
}