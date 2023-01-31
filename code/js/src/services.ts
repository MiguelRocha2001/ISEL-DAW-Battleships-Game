import {links} from './server_info/links'
import {Action, Siren} from './utils/siren'
import {doFetch, Fetch, KeyValuePair} from './utils/useFetch'
import {Logger} from "tslog";
import {CreateGameRequest, CreateGameResponse, Match, PlaceShipsRequest} from './domain'
import {State, useFetchReducer} from "./utils/useFetch-reducer";
import {NetworkError, ServerError} from "./utils/domain";
import {LogLevel} from "ts-loader/dist/logger";

const logger = new Logger({name: "Services"});
logger.settings.minLevel = 3 // LogLevel: INFO

async function fetchHome(): Promise<void | Error> {
    const defaultUrl = links.defaultUrl
    const request = { url: defaultUrl, method: "GET" }
    try {
        const resp = await doFetch(request)
        if (resp instanceof ServerError) {
            return logAndGetError('fetchHome', resp)
        }
        extractSirenInfo(resp)
    } catch (e) {
        return logAndGetError('fetchHome', e)
    }
}

function useFetchHome(): any | Fetching | Error {
    const defaultUrl = links.defaultUrl
    if (defaultUrl) {
        const state = useFetchReducer({url: defaultUrl, method: "GET"})
        return handlerOrError("useFetchHome", state, (siren: Siren) => {
            logger.debug("fetchHome: response successful")
            return siren.properties
        })
    }
    return logAndGetError('useFetchHome', new ResolutionLinkError('default url not found'))
}

function extractSirenInfo(resp: Siren) {
    const serverInfoLink = Siren.extractInfoLink(resp.links)
    const battleshipRanksLink = Siren.extractBattleshipRanksLink(resp.links)
    const getUserLink = Siren.extractGetUserLink(resp.links)
    const userHomeLink = Siren.extractUserHomeLink(resp.links)
    const tokenAction = Siren.extractTokenAction(resp.actions)
    const registerAction = Siren.extractRegisterAction(resp.actions)
    const createGameAction = Siren.extractCreateGameAction(resp.actions)
    const getCurrentGameIdLink = Siren.extractGetCurrentGameIdLink(resp.links)
    const getGameLink = Siren.extractGetGameLink(resp.links)
    const getQuitGameAction = Siren.extractQuitGameAction(resp.actions)

    if (serverInfoLink)
        logger.debug("fetchHome: setting up new info endpoint: ", serverInfoLink)
    if (battleshipRanksLink)
        logger.info("fetchHome: setting up new battleship ranks endpoint: ", battleshipRanksLink)
    if (getUserLink)
        logger.debug("fetchHome: setting up new get user endpoint: ", getUserLink)
    if (userHomeLink)
        logger.debug("fetchHome: setting up new user home endpoint: ", userHomeLink)
    if (tokenAction)
        logger.debug("fetchHome: setting up new token action: ", tokenAction.name)
    if (registerAction)
        logger.debug("fetchHome: setting up new register action: ", registerAction.name)
    if (createGameAction)
        logger.debug("fetchHome: setting up new create game action: ", createGameAction.name)
    if (getCurrentGameIdLink)
        logger.debug("fetchHome: setting up new get current game id link: ", getCurrentGameIdLink)
    if (getGameLink)
        logger.debug("fetchHome: setting up new get game link: ", getGameLink)
    if (getQuitGameAction)
        logger.debug("fetchHome: setting up new quit game action: ", getQuitGameAction.name)

    links.setInfoLink(serverInfoLink)
    links.setBattleshipRanksLink(battleshipRanksLink)
    links.setUserLink(getUserLink)
    links.setUserHomeLink(userHomeLink)
    links.setTokenAction(tokenAction)
    links.setRegisterAction(registerAction)
    links.setCreateGameAction(createGameAction)
    links.setCurrentGameIdLink(getCurrentGameIdLink)
    links.setGetGameLink(getGameLink)
    links.setQuitGameAction(getQuitGameAction)
}

export type ServerInfo = {
    authors: Array<Author>
    systemVersion: string
}
export type Author = {
    name: string
    email: string
}
function useFetchServerInfo(): ServerInfo | Fetching | Error {
    const infoLink = links.getInfoLink()
    if (infoLink) {
        const state = useFetchReducer({url: infoLink, method: "GET"})
        return handlerOrError("useFetchServerInfo", state, (siren: Siren) => {
            return siren.properties
        })
    }
    return logAndGetError('useFetchServerInfo', new ResolutionLinkError('info link not found'))
}

export type Rankings = {
    users: Array<UserStats>
}
export type UserStats = {
    id: number
    username: string
    gamesPlayed: number
    wins: number
}
function useFetchBattleshipRanks(page: number, pageSize: number): Rankings | Fetching | Error {
    const ranksLink = links.getBattleshipRanksLink()
    const ranksLinkWithParams = ranksLink + `?page=${page}&pageSize=${pageSize}`
    if (ranksLink) {
        const state = useFetchReducer({url: ranksLinkWithParams, method: "GET"})
        return handlerOrError("useFetchBattleshipRanks", state, (siren: Siren) => {
            return siren.properties
        })
    }
    return logAndGetError('useFetchBattleshipRanks', new ResolutionLinkError('battleship ranks link not found'))
}

function useGetUser(userId: number): UserStats | Error {
    const oldLink = links.getUserLink()
    const userLink = oldLink.replace(':id', userId.toString())
    if (userLink) {
        const state = useFetchReducer({url: userLink, method: "GET"})
        return handlerOrError('useGetUser', state, (siren: Siren) => {
            return siren.properties
        })
    }
    return logAndGetError('useGetUser', new ResolutionLinkError('user link not found'))
}

async function doLogin(fields: KeyValuePair[]): Promise<void | Error> {
    const action = links.getTokenAction()
    if (action) {
        const request = action ? {
            url: action.href,
            method: action.method,
            body: fields
        } : undefined
        try {
            const resp = await doFetch(request)
            if (resp instanceof ServerError) {
                return logAndGetError('doLogin', resp)
            }
            logger.debug("fetchToken: response successfully")
            const createGameAction = Siren.extractCreateGameAction(resp.actions)
            const userHomeLink = Siren.extractUserHomeLink(resp.links)
            if (createGameAction) {
                links.setCreateGameAction(createGameAction)
                logger.debug("fetchToken: setting up new create game action: ", createGameAction.name)
            } else {
                logger.error("fetchToken: create game action not found in response")
            }
            if (userHomeLink) {
                links.setUserHomeLink(userHomeLink)
                logger.debug("fetchToken: setting up new user home link: ", userHomeLink)
            } else {
                logger.error("fetchToken: user home link not found in response")
            }
            logger.debug("login successful")
            return
        } catch (e) {
            return logAndGetError('doLogin', e)
        }
    }
    return logAndGetError('doLogin', new ResolutionLinkError('token action not found'))
}

/**
 * @param fields
 * @return {Promise<void | Error>} if successful, returns undefined, otherwise returns an error
 */
async function createUser(fields: KeyValuePair[]): Promise<undefined | Error> {
    const action = links.getRegisterAction()
    if (action) {
        const request = action ? {
            url: action.href,
            method: action.method,
            body: fields
        } : undefined
        try {
            const resp = await doFetch(request)
            if (resp instanceof ServerError) {
                return logAndGetError('createUser', resp)
            }
            const userId = resp.properties.userId
            logger.debug("createUser: response successfully")
            const tokenAction = Siren.extractTokenAction(resp.actions)
            if (tokenAction) {
                links.setTokenAction(tokenAction)
                logger.debug("createUser: setting up new token action: ", tokenAction.name)
            } else {
                logger.error("createUser: token action not found in response")
            }
            return userId
        } catch (e) {
            return logAndGetError('createUser', e)
        }
    }
    return logAndGetError('createUser', new ResolutionLinkError('register action not found'))
}

function useFetchUserHome(): UserStats | Fetching | Error {
    const userHomeLink = links.getUserHomeLink()
    if (userHomeLink) {
        const request = {
            url: userHomeLink,
            method: "GET",
        }
        const resp = useFetchReducer(request)
        return handlerOrError('useFetchUserHome', resp, (siren: Siren) => {
            const createGameAction = Siren.extractCreateGameAction(siren.actions)
            const getCurrentGameIdLink = Siren.extractGetCurrentGameIdLink(siren.links)
            const getGameLink = Siren.extractGetGameLink(siren.links)

            if (createGameAction) {
                links.setCreateGameAction(createGameAction)
                logger.debug("fetchUserHome: setting up new create game action: ", createGameAction.name)
            } else {
                return logAndGetError('useFetchUserHome', new ResolutionLinkError('create game action not found'))
            }
            if (getCurrentGameIdLink) {
                links.setCurrentGameIdLink(getCurrentGameIdLink)
                logger.debug("fetchUserHome: setting up new get_current_game_id link: ", getCurrentGameIdLink)
            } else {
                return logAndGetError('useFetchUserHome', new ResolutionLinkError('get_current_game_id link not found'))
            }
            if (getGameLink) {
                links.setGetGameLink(getGameLink)
                logger.debug("fetchUserHome: setting up new get_game link: ", getGameLink)
            } else {
                return logAndGetError('useFetchUserHome', new ResolutionLinkError('get_game link not found'))
            }
            return siren.properties
        })
    }
    return logAndGetError('useFetchUserHome', new ResolutionLinkError('user home link not found'))
}

async function createGame(request: CreateGameRequest | undefined): Promise<CreateGameResponse | Error> {
    const action = links.getCreateGameAction()
    if (!action)
        return logAndGetError('createGame', new ResolutionLinkError('create game action not found'))

    // TODO request could be undefined
    if (Siren.validateFields(request, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody(request),
        }
        try {
            const result = await doFetch(internalReq)
            if (result instanceof ServerError) {
                return logAndGetError('createGame', result)
            }
            const getGameLink = Siren.extractGetGameLink(result.links)
            // const getCurrentGameIdLink = Siren.extractGetCurrentGameIdLink(result.links)
            if (getGameLink) {
                links.setGetGameLink(getGameLink)
                logger.debug("createGame: setting up new get game link: ", getGameLink)
            }
            const createGameResponse = result.properties
            if (createGameResponse) {
                logger.info("createGame: responde sucessfull")
                return createGameResponse
            } else {
                logger.error("createGame: create game response not found")
            }
        } catch (e) {
            return logAndGetError('createGame', e)
        }
    }
    return logAndGetError('createGame', new InvalidArgumentError('Invalid create game request'))
}

async function getCurrentGameId(): Promise<number | Error> {
    const currentGameIdLink = links.getCurrentGameIdLink()
    if (!currentGameIdLink)
        return logAndGetError('getCurrentGameId', new ResolutionLinkError('current game id link not found'))

    try {
        const result = await Fetch.doFetch({ url: currentGameIdLink, method: "GET", body: undefined })
        if (result instanceof ServerError) {
            return logAndGetError('getCurrentGameId', result)
        }
        logger.debug("getGame: response successful")
        const gameId = result.properties.id
        if (gameId) {
            return gameId
        } else {
            logger.error("getGame: game id not found in response")
        }
    } catch (e) {
        return logAndGetError('getGame', e)
    }
}

async function getGame(): Promise<Match | Error> {

    function checkLinks(response: Siren): string {
        const placeShipsAction = Siren.extractPlaceShipsAction(response.actions)
        const confirmFleetAction = Siren.extractConfirmFleetAction(response.actions)
        const attackAction = Siren.extractAttackAction(response.actions)
        const quitGameAction = Siren.extractQuitGameAction(response.actions)
        if (placeShipsAction) {
            links.setPlaceShipsAction(placeShipsAction)
            logger.debug("getGame: setting up new place ships action: ", placeShipsAction.name)
        } else {
            logger.error("getGame: place ships action not found in response")
        }
        if (confirmFleetAction) {
            links.setConfirmFleetAction(confirmFleetAction)
            logger.debug("getGame: setting up new confirm fleet action: ", confirmFleetAction.name)
        } else {
            logger.error("getGame: confirm fleet action not found in response")
        }
        if (attackAction) {
            links.setAttackAction(attackAction)
            logger.debug("getGame: setting up new attack action: ", attackAction.name)
        } else {
            logger.error("getGame: attack action not found in response")
        }
        if (quitGameAction) {
            links.setQuitGameAction(quitGameAction)
            logger.debug("getGame: setting up new quit game action: ", quitGameAction.name)
        } else {
            logger.error("getGame: quit game action not found in response")
        }
        return undefined
    }

    function fromSirenPropsToMatch(props: any): Match {
        return new Match(
            props.id,
            props.configuration,
            props.player1,
            props.player2,
            props.state,
            props.board1,
            props.board2,
            props.winner,
            props.playerTurn,
            props.localPlayer
        )
    }

    const gameLink = links.getGameLink()
    if (!gameLink)
        return logAndGetError('getGame', new ResolutionLinkError('game link not found'))

    try {
        const response = await Fetch.doFetch({ url: gameLink, method: "GET", body: undefined })
        if (response instanceof ServerError) {
            return logAndGetError('getGame', response)
        }
        const error = checkLinks(response) // returns a string if there is an error
        if (error)
            return new Error(error)
        logger.debug("getGame: response successful")
        return fromSirenPropsToMatch(response.properties)
    } catch (e) {
        return logAndGetError('getGame', e)
    }
}

// TODO -> fails when parsing placeShipsRequest to JSON (but server doesnt responds well)
async function placeShips(placeShipsRequest: PlaceShipsRequest): Promise<void | Error> {
    const action = links.getPlaceShipsAction()
    if (!action)
        return logAndGetError('placeShips', new ResolutionLinkError('place ships action not found'))

    if (Siren.validateFields(placeShipsRequest, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody(placeShipsRequest),
        }
        try {
            const result = await doFetch(internalReq)
            if (result instanceof ServerError) {
                return logAndGetError('placeShips', result)
            }
            logger.debug("placeShips: response successful")
        } catch (e) {
            return logAndGetError('placeShips', e)
        }
    } else
        return logAndGetError('placeShips', new InvalidArgumentError('place ships request not valid'))
}
async function confirmFleet(): Promise<void | Error> {
    const action = links.getConfirmFleetAction()
    if (!action)
        return logAndGetError('confirmFleet', new ResolutionLinkError('confirm fleet action not found'))

    const request = {fleetConfirmed: true} // request is set here because it is always the same
    if (Siren.validateFields(request, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody({fleetConfirmed: true}),
        }
        try {
            const result = await doFetch(internalReq)
            if (result instanceof ServerError) {
                return logAndGetError('confirmFleet', result)
            }
            logger.debug("confirmFleet: response successful")
        } catch (e) {
            return logAndGetError('confirmFleet', e)
        }
    } else
        return logAndGetError('confirmFleet', new InvalidArgumentError('confirm fleet action not found'))
}

async function attack(attackRequest: any): Promise<void | Error> {
    const action = links.getAttackAction()
    if (!action)
        return logAndGetError('attack', new ResolutionLinkError('attack action not found'))

    if (Siren.validateFields(attackRequest, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody(attackRequest),
        }
        try {
            const result = await doFetch(internalReq)
            if (result instanceof ServerError) {
                return logAndGetError('attack', result)
            }
            logger.info("attack: response successful")
        } catch (e) {
            return logAndGetError('attack', e)
        }
    } else
        return logAndGetError('attack', new InvalidArgumentError('attack action not found'))
}

async function quitGame(gameId: number): Promise<void | Error> {
    const action = links.getQuitGameAction()
    if (!action)
        return logAndGetError('quitGame', new ResolutionLinkError('quit game action not found'))

    const template = action.href
    const href = template.replace(':id', gameId.toString())
    const request = {} // request is set here because it is always the same
    if (Siren.validateFields(request, action)) {
        const internalReq = {
            url: href,
            method: action.method,
            body: Fetch.toBody(request),
        }
        try {
            const result = await doFetch(internalReq)
            if (result instanceof ServerError) {
                return logAndGetError('quitGame', result)
            }
            logger.debug("quitGame: response successful")
        } catch (e) {
            return logAndGetError('quitGame', e)
        }
    } else
        return logAndGetError('quitGame', new ResolutionLinkError('quit game action not found'))
}

export class Fetching {}
function handlerOrError(origin: string, state: State, handler: (siren: Siren) => any): any | Error | Fetching {
    switch (state.type) {
        case 'response' : {
            logger.debug(origin, "response successful")
            return handler(state.response)
        }
        case "fetching" : {
            return new Fetching()
        }
        case "networkError" : {
            return logAndGetNetworkError(origin, new NetworkError(state.error))
        }
        case "serverError" : {
            return logAndGetServerError(origin, new ServerError(state.error, state.status))
        }
        case 'started' : {
            return new Fetching()
        }
    }
}

export class ResolutionLinkError extends Error {
    constructor(message: string) {
        super(message)
        this.name = "ResolutionLinkError"
    }
}

export class InvalidArgumentError extends Error {
    constructor(message: string) {
        super(message)
        this.name = "InvalidArgumentError"
    }

}

function logAndGetError(origin: string, error: Error): Error {
    if (error instanceof ServerError) {
        return logAndGetServerError(origin, error)
    } else if (error instanceof NetworkError) {
        return logAndGetNetworkError(origin, error)
    } else if (error instanceof ResolutionLinkError) {
        return logAndGetResolutionLinkError(origin, error)
    } else if (error instanceof InvalidArgumentError) {
        return logAndGetInvalidArgumentError(origin, error)
    }
}

function logAndGetNetworkError(origin: string, error: NetworkError): NetworkError {
    logger.error(`${origin}: Network Error: ${error.message}`)
    return error
}

function logAndGetResolutionLinkError(origin: string, error: ResolutionLinkError): ResolutionLinkError {
    logger.error(`${origin}: Resolution Link Error: ${error.message}`)
    return error
}

function logAndGetServerError(origin: string, error: ServerError): ServerError {
    logger.error(`${origin}: Server Error: ${error.message}`)
    return error
}

function logAndGetInvalidArgumentError(origin: string, error: InvalidArgumentError): InvalidArgumentError {
    logger.error(`${origin}: Invalid Argument Error: ${error.message}`)
    return error
}

export const Services = {
    useFetchHome,
    fetchHome,
    useFetchServerInfo,
    useFetchBattleshipRanks,
    useGetUser,
    doLogin,
    createUser,
    createGame,
    getCurrentGameId,
    getGame,
    fetchUserHome: useFetchUserHome,
    placeShips,
    confirmFleet,
    attack,
    quitGame
}
