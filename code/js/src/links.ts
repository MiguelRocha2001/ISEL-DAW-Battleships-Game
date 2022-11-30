const host = 'http://localhost:8080/api'

const defaultUrl = host

let infoLink: string | undefined
let battleshipRanksLink: string | undefined

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

export const links = {
    host,
    defaultUrl,
    getInfoLink,
    setInfoLink,
    getBattleshipRanksLink,
    setBattleshipRanksLink,
}