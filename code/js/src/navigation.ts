import { links } from './server_info/links'
import { auth } from './server_info/auth'
import { Siren } from './utils/siren'
import { Action } from './utils/siren'
import { Link } from './utils/siren'
import { useFetch } from './utils/useFetch'
import { Logger } from "tslog";
import { ActionInput } from './utils/useFetch'

const logger = new Logger({ name: "Navigation" });


function useFetchHome(): Siren | undefined {
    const defaultUrl = links.defaultUrl
    const resp = useFetch({ url: defaultUrl, method: "GET" })
    
    if (resp) {
        logger.info("fetchHome: responde sucessfull")
        const serverInfoLink = extractInfoLink(resp.links)
        const battleshipRanksLink = extractBattleshipRanksLink(resp.links)
        const tokenAction = extractTokenAction(resp.actions)
        const registerAction = extractRegisterAction(resp.actions)
        if (serverInfoLink)
            logger.info("fetchHome: setting up new info endpoint: ", serverInfoLink)
        if (battleshipRanksLink)
            logger.info("fetchHome: setting up new battleship ranks endpoint: ", battleshipRanksLink)
        if (tokenAction)
            logger.info("fetchHome: setting up new token action: ", tokenAction.name)
        if (registerAction)
            logger.info("fetchHome: setting up new register action: ", registerAction.name)
        links.setInfoLink(serverInfoLink)
        links.setBattleshipRanksLink(battleshipRanksLink)
        links.setTokenAction(tokenAction)
    }
    return resp
}

function extractInfoLink(linksArg: Link[]): string | undefined {
    if (!linksArg) return undefined
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
                return link.href
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

function extractRegisterAction(actions: any[]): Action {
    for (let i = 0; i < actions.length; i++) {
        const action = actions[i]
        if (action.name === "create-user") {
            return action
        }
    }
    return undefined
}

function useFetchServerInfo(): Siren | undefined {
    const infoLink = links.getInfoLink()
    const resp = useFetch({ url: infoLink, method: "GET" })
    if (resp) {
        logger.info("fetchServerInfo: responde sucessfull")
        return resp
    } else return undefined
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
    const resp = useFetchHome()
    if (resp) {
        const link = extractBattleshipRanksLink(resp.links)
        return useFetch({ url: link, method: "GET" })
    }
    return undefined
}

function useFetchToken(fields: ActionInput[]): string {
    const action = links.getTokenAction()
    const request = action ? {
        url: action.href,
        method: action.method,
        body: fields
    } : undefined
    const resp = useFetch(request)
    if (resp) {
        const token = resp.properties.token
        if (token) {
            logger.info("fetchToken: responde sucessfull")
            auth.setToken(token)
            logger.info("fetchToken: token set to: ", token)
            return token
        } else {
            logger.error("fetchToken: token not found in response")
            return undefined
        }
    }
    return undefined
}

async function useRegisterNewUser(fields: ActionInput[]) {
    function useRegisterNewUserInternal(fields: ActionInput[], action: Action) {
        if (validateFields(fields, action)) {
            const request = {
                url: action.href,
                method: action.method,
                body: fields
            }
            const resp = useFetch(request)
            if (resp) {
                logger.info("registerUser: responde sucessfull")
                return resp
            }
        }
    }
    const action = links.getRegisterAction()
    if (action) {
        useRegisterNewUserInternal(fields, action)
    } else {
        const resp = useFetchHome()
        if (resp) {
            const action = extractRegisterAction(resp.actions)
            useRegisterNewUserInternal(fields, action)
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

function isLogged(): boolean {
    return auth.getToken() !== undefined
}

export const navigation = {
    useFetchHome,
    useFetchServerInfo,
    fetchBattleshipRanks,
    useFetchToken,
    useRegisterNewUser,
    isLogged
}