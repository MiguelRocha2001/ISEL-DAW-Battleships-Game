import { links } from './server_info/links'
import { auth } from './server_info/auth'
import { Siren } from './utils/siren'
import { Action } from './utils/siren'
import { useFetch } from './utils/useFetch'
import { doFetch } from './utils/useFetch'
import { Fetch } from './utils/useFetch'
import { Logger } from "tslog";
import { KeyValuePair } from './utils/useFetch'
import {CreateGameRequest, CreateGameResponse, Game, PlaceShipsRequest} from './domain'

const logger = new Logger({ name: "Navigation" });


function useFetchHome(): Siren | undefined {
    const defaultUrl = links.defaultUrl
    const resp = useFetch({ url: defaultUrl, method: "GET" })
    
    if (resp) {
        logger.info("fetchHome: responde sucessfull")
        const serverInfoLink = Siren.extractInfoLink(resp.links)
        const battleshipRanksLink = Siren.extractBattleshipRanksLink(resp.links)
        const tokenAction = Siren.extractTokenAction(resp.actions)
        const registerAction = Siren.extractRegisterAction(resp.actions)
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
        const link = Siren.extractBattleshipRanksLink(resp.links)
        return useFetch({ url: link, method: "GET" })
    }
    return undefined
}

async function fetchToken(fields: KeyValuePair[]): Promise<string | undefined> {
    const action = links.getTokenAction()
    const request = action ? {
        url: action.href,
        method: action.method,
        body: fields
    } : undefined
    const resp = await doFetch(request)
    if (resp) {
        const token = resp.properties.token
        if (token) {
            logger.info("fetchToken: responde sucessfull")
            const createGameAction = Siren.extractCreateGameAction(resp.actions)
            const userHomeLink = Siren.extractUserHomeLink(resp.links)
            if (createGameAction) {
                links.setCreateGameAction(createGameAction)
                logger.info("fetchToken: setting up new create game action: ", createGameAction.name)
            } else {
                logger.error("fetchToken: create game action not found in response")
            }
            if (userHomeLink) {
                links.setUserHomeLink(userHomeLink)
                logger.info("fetchToken: setting up new user home link: ", userHomeLink)
            } else {
                logger.error("fetchToken: user home link not found in response")
            }
            auth.setToken(token)
            logger.info("fetchToken: token set to: ", token)
            return token
        } else {
            logger.error("fetchToken: token not found in response")
            return undefined
        }
    }
}

async function useRegisterNewUser(fields: KeyValuePair[]) {
    function useRegisterNewUserInternal(fields: KeyValuePair[], action: Action) {
        if (Siren.validateFields(fields, action)) {
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
            const action = Siren.extractRegisterAction(resp.actions)
            useRegisterNewUserInternal(fields, action)
        }
    }
}

async function fetchUserHome(): Promise<void | string> {
    const token = auth.getToken()
    const userHomeLink = links.getUserHomeLink()
    if (token && userHomeLink) {
        const request = {
            url: userHomeLink,
            method: "GET",
            token
        }
        const resp = await doFetch(request)
        if (resp) {
            logger.info("fetchUserHome: response sucessfull")
            const createGameAction = Siren.extractCreateGameAction(resp.actions)
            const getCurrentGameIdLink = Siren.extractGetCurrentGameIdLink(resp.links)
            const getGameLink = Siren.extractGetGameLink(resp.links)

            if (createGameAction) {
                links.setCreateGameAction(createGameAction)
                logger.info("fetchUserHome: setting up new create game action: ", createGameAction.name)
            } else {
                logger.error("fetchUserHome: create game action not found in response")
                return "Couldnt find create game action"
            }
            if (getCurrentGameIdLink) {
                links.setCurrentGameIdLink(getCurrentGameIdLink)
                logger.info("fetchUserHome: setting up new get_current_game_id link: ", getCurrentGameIdLink)
            } else {
                logger.error("fetchUserHome: get_current_game_id link not found in response")
                return "Couldnt find get current game id link"
            }
            if (getGameLink) {
                links.setGetGameLink(getGameLink)
                logger.info("fetchUserHome: setting up new get_game link: ", getGameLink)
            } else {
                logger.error("fetchUserHome: get_game link not found in response")
                return "Couldnt find get game link"
            }
            return
        }
        return "fetchUserHome: response undefined"
    }
    return "fetchUserHome: token or user home link undefined"
}

function isLogged(): boolean {
    return auth.getToken() !== undefined
}

async function createGame(request: CreateGameRequest): Promise<CreateGameResponse | string> {
    const token = auth.getToken()
    const action = links.getCreateGameAction()
    if (!token || !action) {
        logger.error("createGame: token or create game action undefined")
        return "createGame: token or create game action undefined"
    }
    if (Siren.validateFields(request, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody(request),
            token
        }
        try {
            const siren = await doFetch(internalReq)
            if (siren) {
                const getGameLink = Siren.extractGetGameLink(siren.links)
                // const getCurrentGameIdLink = Siren.extractGetCurrentGameIdLink(siren.links)
                if (getGameLink) {
                    links.setGetGameLink(getGameLink)
                    logger.info("createGame: setting up new get game link: ", getGameLink)
                }
                const createGameResponse = siren.properties
                if (createGameResponse) {
                    logger.info("createGame: responde sucessfull")
                    return createGameResponse
                } else {
                    logger.error("createGame: gameId not found in response")
                    return 'GameId not found in response'
                }
            }
        } catch (e) {
            logger.error("createGame: error: ", e.title)
            return e.title
        }
    }
    return 'createGameRequest not valid'
}

async function getCurrentGameId(): Promise<number | string> {
    const token = auth.getToken()
    const currentGameIdLink = links.getCurrentGameIdLink()
    if (!token || !currentGameIdLink) return 'Token or get-current-game-id link undefined'
    try {
        const response = await Fetch.doFetch({ url: currentGameIdLink, method: "GET", body: undefined, token })
        if (response) {
            logger.info("getGame: response sucessfull")
            const gameId = response.properties.gameId
            if (gameId) {
                return gameId
            } else {
                logger.error("getCurrentGameId: gameId not found in response")
                return 'GameId not found in response'
            }
        }
    } catch (e) {
        logger.error("getCurrentGameId: error: ", e)
        return e.message.toString()
    }
}

async function getGame(): Promise<Game | string> {
    const token = auth.getToken()
    const gameLink = links.getGameLink()
    if (!token || !gameLink) return 'Token or get-game link not found'
    try {
        const response = await Fetch.doFetch({ url: gameLink, method: "GET", body: undefined, token })
        if (response) {
            logger.info("getGame: response sucessfull")
            return response.properties
        } else {
            logger.error("getGame: bad response")
            return 'Received bad response'
        }
    } catch (e) {
        logger.error("getGame: error: ", e)
        return 'Bad response from server'
    }
}

async function placeShips(placeShipsRequest: PlaceShipsRequest): Promise<void | string> {
    const token = auth.getToken()
    const action = links.getPlaceShipsAction()
    if (!token || !action) {
        logger.error("placeShips: token or place ships action undefined")
        return "placeShips: token or place ships action undefined"
    }
    if (Siren.validateFields(placeShipsRequest, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody(placeShipsRequest),
            token
        }
        try {
            const siren = await doFetch(internalReq)
            if (siren) {
                logger.info("placeShips: response successful")
                return
            }
        } catch (e) {
            logger.error("placeShips: error: ", e.title)
            return e.title
        }
    }
    return 'placeShipsRequest not valid'
}

async function confirmFleet(): Promise<void | string> {
    const token = auth.getToken()
    const action = links.getConfirmFleetAction()
    if (!token || !action) {
        logger.error("confirmFleet: token or confirm fleet action undefined")
        return "confirmFleet: token or confirm fleet action undefined"
    }
    const internalReq = {
        url: action.href,
        method: action.method,
        body: undefined,
        token
    }
    try {
        const siren = await doFetch(internalReq)
        if (siren) {
            logger.info("confirmFleet: response successful")
            return
        }
    } catch (e) {
        logger.error("confirmFleet: error: ", e.title)
        return e.title
    }
}

export const Services = {
    useFetchHome,
    useFetchServerInfo,
    fetchBattleshipRanks,
    fetchToken,
    useRegisterNewUser,
    isLogged,
    createGame,
    getCurrentGameId,
    getGame,
    fetchUserHome,
    placeShips,
    confirmFleet
}