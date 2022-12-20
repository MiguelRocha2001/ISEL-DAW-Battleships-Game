import { links } from './server_info/links'
import { auth } from './server_info/auth'
import { Siren } from './utils/siren'
import { Action } from './utils/siren'
import { doFetch } from './utils/useFetch'
import { Fetch } from './utils/useFetch'
import { Logger } from "tslog";
import { KeyValuePair } from './utils/useFetch'
import {CreateGameRequest, CreateGameResponse, Game, PlaceShipsRequest, Position} from './domain'
import {State, useFetchNew} from "./utils/useFetch-reducer";

const logger = new Logger({ name: "Navigation" });


function useFetchHome(): any | string {
    const defaultUrl = links.defaultUrl
    const state = useFetchNew({ url: defaultUrl, method: "GET" })
    return handlerOrError(state, (siren: Siren) => {
        logger.info("fetchHome: response sucessfull")
        const serverInfoLink = Siren.extractInfoLink(siren.links)
        const battleshipRanksLink = Siren.extractBattleshipRanksLink(siren.links)
        const tokenAction = Siren.extractTokenAction(siren.actions)
        const registerAction = Siren.extractRegisterAction(siren.actions)
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
        return siren.properties
    })
}

function useFetchServerInfo(): Siren | string {
    const infoLink = links.getInfoLink()
    const state = useFetchNew({ url: infoLink, method: "GET" })
    return handlerOrError(state, (siren: Siren) => {
        logger.info("fetchServerInfo: response sucessfull")
        return siren.properties
    })
}

function useFetchBattleshipRanks(): Siren | string {
    const ranksLink = links.getBattleshipRanksLink()
    if (ranksLink) {
        const state = useFetchNew({url: ranksLink, method: "GET"})
        return handlerOrError(state, (siren: Siren) => {
            logger.info("fetchServerInfo: response successful")
            return siren.properties
        })
    }
    logger.error("useFetchBattleshipRanks: link not found")
    return 'Please, return to home page'
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

    function checkLinks(response: Siren): string {
        const placeShipsAction = Siren.extractPlaceShipsAction(response.actions)
        const confirmFleetAction = Siren.extractConfirmFleetAction(response.actions)
        const attackAction = Siren.extractAttackAction(response.actions)
        if (placeShipsAction) {
            links.setPlaceShipsAction(placeShipsAction)
            logger.info("getGame: setting up new place ships action: ", placeShipsAction.name)
        } else {
            logger.error("getGame: place ships action not found in response")
            return "Couldn't find place ships action"
        }
        if (confirmFleetAction) {
            links.setConfirmFleetAction(confirmFleetAction)
            logger.info("getGame: setting up new confirm fleet action: ", confirmFleetAction.name)
        } else {
            logger.error("getGame: confirm fleet action not found in response")
            return "Couldn't find confirm fleet action"
        }
        if (attackAction) {
            links.setAttackAction(attackAction)
            logger.info("getGame: setting up new attack action: ", attackAction.name)
        } else {
            logger.error("getGame: attack action not found in response")
            return "Couldn't find attack action"
        }
        return undefined
    }


    const token = auth.getToken()
    const gameLink = links.getGameLink()
    if (!token || !gameLink) return 'Token or get-game link not found'
    try {
        const response = await Fetch.doFetch({ url: gameLink, method: "GET", body: undefined, token })
        if (response) {
            const error = checkLinks(response) // returns a string if there is an error
            if (error) return error
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


// TODO -> fails when parsing placeShipsRequest to JSON (but server doesnt responds well)
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
    console.log('Action', action)
    if (!token || !action) {
        logger.error("confirmFleet: token or confirm fleet action undefined")
        return "confirmFleet: token or confirm fleet action undefined"
    }
    const request = {fleetConfirmed: true} // request is set here because it is always the same
    if (Siren.validateFields(request, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody({fleetConfirmed: true}),
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
}

async function attack(attackRequest: Position): Promise<void | string> {
    const token = auth.getToken()
    const action = links.getAttackAction()
    if (!token || !action) {
        logger.error("attack: token or attack action undefined")
        return "attack: token or attack action undefined"
    }
    if (Siren.validateFields(attackRequest, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody(attackRequest),
            token
        }
        try {
            const siren = await doFetch(internalReq)
            if (siren) {
                logger.info("attack: response successful")
                return
            }
        } catch (e) {
            logger.error("attack: error: ", e.title)
            return e.title
        }
    }
    return 'attackRequest not valid'
}

function handlerOrError(state: State, handler: (siren: Siren) => any): any | string {
    switch (state.type) {
        case 'response' : {
            return handler(state.response)
        }
        case "fetching" : {
            return 'Loading'
        }
        case "error" : {
            return 'Error'
        }
        case 'started' : {
            return 'Loading'
        }
    }
}

export const Services = {
    useFetchHome,
    useFetchServerInfo,
    fetchBattleshipRanks: useFetchBattleshipRanks,
    fetchToken,
    useRegisterNewUser,
    isLogged,
    createGame,
    getCurrentGameId,
    getGame,
    fetchUserHome,
    placeShips,
    confirmFleet,
    attack
}
