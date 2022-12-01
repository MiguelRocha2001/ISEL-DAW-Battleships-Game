import { links } from './links'
import { Siren } from './utils/siren'
import { Action } from './utils/siren'
import { Link } from './utils/siren'
import { useFetch } from './utils/useFetch'
import { Logger } from "tslog";
import { ActionInput } from './utils/useFetch'

const logger = new Logger({ name: "Navigation" });

function fetchHome(): Siren | undefined {
    const defaultUrl = links.defaultUrl
    const resp = useFetch({ url: defaultUrl, method: "GET" })
    if (resp) {
        logger.info("fetchHome: responde sucessfull")
        const serverInfoLink = extractInfoLink(resp.links)
        const battleshipRanksLink = extractBattleshipRanksLink(resp.links)
        const tokenAction = extractTokenAction(resp.actions)
        if (serverInfoLink)
            logger.info("fetchHome: setting up new info endpoint: ", serverInfoLink)
        if (battleshipRanksLink)
            logger.info("fetchHome: setting up new battleship ranks endpoint: ", battleshipRanksLink)
        if (tokenAction)
            logger.info("fetchHome: setting up new token action: ", tokenAction.name)
        links.setInfoLink(serverInfoLink)
        links.setBattleshipRanksLink(battleshipRanksLink)
        links.setTokenAction(tokenAction)
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

function extractTokenAction(actions: any[]): Action {
    for (let i = 0; i < actions.length; i++) {
        const action = actions[i]
        if (action.name === "create-token") {
            return action
        }
    }
    return undefined
}

function fetchServerInfo(): Siren | undefined {
    const infoLink = links.getInfoLink()
    if (infoLink) {
        const resp = useFetch({ url: infoLink, method: "GET" })
        if (resp) {
            logger.info("fetchServerInfo: responde sucessfull")
            return resp
        } else return undefined
    }
    const resp = fetchHome()
    if (resp) {
        const link = extractInfoLink(resp.links)
        return useFetch({ url: link, method: "GET" })
    }
    return undefined
}

function fetchBattleshipRanks(): Siren | undefined {
    const ranksLink = links.getBattleshipRanksLink()
    if (ranksLink) {
        const resp = useFetch({ url: ranksLink, method: "GET" })
        if (resp) {
            logger.info("fetchBattleshipRanks: responde sucessfull")
            return resp
        } else return undefined
    }
    const resp = fetchHome()
    if (resp) {
        const link = extractBattleshipRanksLink(resp.links)
        return useFetch({ url: link, method: "GET" })
    }
    return undefined
}

function fetchToken(fields: ActionInput[]) {
    function fetchTokenInternal(fields: ActionInput[], action: Action) {
        if (validateFields(fields, action)) {
            const request = {
                url: "POST",
                method: action.method,
                body: fields
            }
            const resp = useFetch(request)
            if (resp) {
                logger.info("fetchToken: responde sucessfull")
                return resp
            } else return undefined
        }
    }
    const action = links.getTokenAction()
    if (action) {
        fetchTokenInternal(fields, action)
    } else {
        const resp = fetchHome()
        if (resp) {
            const action = extractTokenAction(resp.actions)
            fetchTokenInternal(fields, action)
        }
    }
}

/**
 * Validates if all necessary fields, in [action], are present in [fields].
 */
function validateFields(fields: ActionInput[], action: Action): boolean {
    for (let i = 0; i < fields.length; i++) {
        const field = fields[i]
        action.fields.find((f) => f.name === field.name)
    }
    return true
}

export const navigation = {
    fetchHome,
    fetchServerInfo,
    fetchBattleshipRanks,
    fetchToken
}