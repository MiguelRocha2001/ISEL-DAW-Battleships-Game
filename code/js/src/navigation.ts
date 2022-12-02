import { links } from './server_info/links'
import { auth } from './server_info/auth'
import { Siren } from './utils/siren'
import { Action } from './utils/siren'
import { Link } from './utils/siren'
import { doFetch } from './utils/useFetch'
import { Logger } from "tslog";
import { ActionInput } from './utils/useFetch'

const logger = new Logger({ name: "Navigation" });

async function fetchHome(): Promise<Siren> {
    const defaultUrl = links.defaultUrl
    const resp = await doFetch({ url: defaultUrl, method: "GET" })
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

function extractRegisterAction(actions: any[]): Action {
    for (let i = 0; i < actions.length; i++) {
        const action = actions[i]
        if (action.name === "create-user") {
            return action
        }
    }
    return undefined
}

async function fetchServerInfo(): Promise<Siren> {
    const infoLink = links.getInfoLink()
    if (infoLink) {
        const resp = await doFetch({ url: infoLink, method: "GET" })
        if (resp) {
            logger.info("fetchServerInfo: responde sucessfull")
            return resp
        } else return undefined
    }
    const resp = await fetchHome()
    if (resp) {
        const link = extractInfoLink(resp.links)
        return await doFetch({ url: link, method: "GET" })
    }
    return undefined
}

async function fetchBattleshipRanks(): Promise<Siren> {
    const ranksLink = links.getBattleshipRanksLink()
    if (ranksLink) {
        const resp = await doFetch({ url: ranksLink, method: "GET" })
        if (resp) {
            logger.info("fetchBattleshipRanks: responde sucessfull")
            return resp
        } else return undefined
    }
    const resp = await fetchHome()
    if (resp) {
        const link = extractBattleshipRanksLink(resp.links)
        return await doFetch({ url: link, method: "GET" })
    }
    return undefined
}

async function fetchToken(fields: ActionInput[]) {
    async function fetchTokenInternal(fields: ActionInput[], action: Action) {
        if (validateFields(fields, action)) {
            const request = {
                url: "POST",
                method: action.method,
                body: fields
            }
            const resp = await doFetch(request)
            if (resp) {
                const token = resp.properties.token
                if (token) {
                    logger.info("fetchToken: responde sucessfull")
                    auth.setToken(token)
                } else {
                    logger.error("fetchToken: token not found in response")
                }
            }
        }
    }
    const action = links.getTokenAction()
    if (action) {
        await fetchTokenInternal(fields, action)
    } else {
        const resp = await fetchHome()
        if (resp) {
            const action = extractTokenAction(resp.actions)
            await fetchTokenInternal(fields, action)
        }
    }
}

async function registerNewUser(fields: ActionInput[]) {
    async function registerNewUserInternal(fields: ActionInput[], action: Action) {
        if (validateFields(fields, action)) {
            const request = {
                url: "POST",
                method: action.method,
                body: fields
            }
            const resp = await doFetch(request)
            if (resp) {
                logger.info("registerUser: responde sucessfull")
                return resp
            }
        }
    }
    const action = links.getTokenAction()
    if (action) {
        await registerNewUserInternal(fields, action)
    } else {
        const resp = await fetchHome()
        if (resp) {
            const action = extractRegisterAction(resp.actions)
            await registerNewUserInternal(fields, action)
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
    fetchHome,
    fetchServerInfo,
    fetchBattleshipRanks,
    fetchToken,
    registerNewUser,
    isLogged
}