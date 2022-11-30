import { links } from './links'
import { Siren } from './utils/siren'
import { Link } from './utils/siren'
import { useFetch } from './utils/useFetch'
import { Logger } from "tslog";

const logger = new Logger({ name: "Navigation" });

function fetchHome(): Siren | undefined {
    const defaultUrl = links.defaultUrl
    const resp = useFetch(defaultUrl)
    if (resp) {
        logger.info("fetchHome: responde sucessfull")
        const serverInfoLink = extractInfoLink(resp.links)
        const battleshipRanksLink = extractBattleshipRanksLink(resp.links)
        if (serverInfoLink)
            logger.info("fetchHome: setting up new info endpoint: ", serverInfoLink)
        if (battleshipRanksLink)
            logger.info("fetchHome: setting up new battleship ranks endpoint: ", battleshipRanksLink)
        links.setInfoLink(serverInfoLink)
        links.setBattleshipRanksLink(battleshipRanksLink)
    }
    return resp
}

function extractInfoLink(linksArg: Link[]): string {
    return extractLink(linksArg, "server-info")
}

function extractBattleshipRanksLink(linksArg: Link[]): string {
    return extractLink(linksArg, "user-stats")
}

function extractLink(linksArg: Link[], rel: string): string {
    for (let i = 0; i < linksArg.length; i++) {
        const link = linksArg[i]
        for (let j = 0; j < link.rel.length; j++) {
            if (link.rel[j] === rel) {
                return links.host + link.href
            }
        }
    }
    return undefined
}

function fetchServerInfo(): Siren | undefined {
    const infoLink = links.getInfoLink()
    if (infoLink) {
        const resp = useFetch(infoLink)
        if (resp) {
            logger.info("fetchServerInfo: responde sucessfull")
            return resp
        } else return undefined
    }
    const resp = fetchHome()
    if (resp) {
        const link = extractInfoLink(resp.links)
        return useFetch(link)
    }
    return undefined
}

function fetchBattleshipRanks(): Siren | undefined {
    const ranksLink = links.getBattleshipRanksLink()
    if (ranksLink) {
        const resp = useFetch(ranksLink)
        if (resp) {
            logger.info("fetchBattleshipRanks: responde sucessfull")
            return resp
        } else return undefined
    }
    const resp = fetchHome()
    if (resp) {
        const link = extractBattleshipRanksLink(resp.links)
        return useFetch(link)
    }
    return undefined
}

export const navigation = {
    fetchHome,
    fetchServerInfo,
    fetchBattleshipRanks,
}