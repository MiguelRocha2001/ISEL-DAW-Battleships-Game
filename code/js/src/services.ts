import {links} from './server_info/links'
import {Action, Siren} from './utils/siren'
import {doFetch, Fetch, KeyValuePair} from './utils/useFetch'
import {Logger} from "tslog";
import {CreateGameRequest, CreateGameResponse, Match, PlaceShipsRequest} from './domain'
import {State, useFetchReducer} from "./utils/useFetch-reducer";

const logger = new Logger({ name: "Services" });

async function fetchHome(): Promise<void | Error> {
    const defaultUrl = links.defaultUrl
    const request = { url: defaultUrl, method: "GET" }
    try {
        const resp = await doFetch(request)
        if (resp) {
            extractLinksAndActionsFromHomeResponse(resp)
        }
    } catch (e) {
        return loggAndReturnError('fetchHome: error: ' + e)
    }
}

function useFetchHome(): any | Fetching| Error {
    const defaultUrl = links.defaultUrl
    const state = useFetchReducer({ url: defaultUrl, method: "GET" })
    return handlerOrError(state, (siren: Siren) => {
        logger.info("fetchHome: response successful")
        extractLinksAndActionsFromHomeResponse(siren)
        return siren.properties
    })
}

function extractLinksAndActionsFromHomeResponse(resp: Siren) {
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
        logger.info("fetchHome: setting up new info endpoint: ", serverInfoLink)
    if (battleshipRanksLink)
        logger.info("fetchHome: setting up new battleship ranks endpoint: ", battleshipRanksLink)
    if (getUserLink)
        logger.info("fetchHome: setting up new get user endpoint: ", getUserLink)
    if (userHomeLink)
        logger.info("fetchHome: setting up new user home endpoint: ", userHomeLink)
    if (tokenAction)
        logger.info("fetchHome: setting up new token action: ", tokenAction.name)
    if (registerAction)
        logger.info("fetchHome: setting up new register action: ", registerAction.name)
    if (createGameAction)
        logger.info("fetchHome: setting up new create game action: ", createGameAction.name)
    if (getCurrentGameIdLink)
        logger.info("fetchHome: setting up new get current game id link: ", getCurrentGameIdLink)
    if (getGameLink)
        logger.info("fetchHome: setting up new get game link: ", getGameLink)
    if (getQuitGameAction)
        logger.info("fetchHome: setting up new quit game action: ", getQuitGameAction.name)
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
        return handlerOrError(state, (siren: Siren) => {
            logger.info("fetchServerInfo: response successfully")
            return siren.properties
        })
    }
    return loggAndReturnError('useFetchServerInfo: link not found')
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
function useFetchBattleshipRanks(): Rankings | Fetching | Error {
    const ranksLink = links.getBattleshipRanksLink()
    if (ranksLink) {
        const state = useFetchReducer({url: ranksLink, method: "GET"})
        return handlerOrError(state, (siren: Siren) => {
            logger.info("fetchServerInfo: response successful")
            return siren.properties
        })
    }
    return loggAndReturnError('useFetchBattleshipRanks: link not found')
}

function getUser(userId: number): UserStats | Error {
    const oldLink = links.getUserLink()
    const userLink = oldLink.replace(':id', userId.toString())
    if (userLink) {
        const state = useFetchReducer({url: userLink, method: "GET"})
        return handlerOrError(state, (siren: Siren) => {
            logger.info("fetchServerInfo: response successful")
            return siren.properties
        })
    }
    return loggAndReturnError('getUser: link not found')
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
            logger.info("fetchToken: response successfully")
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
            logger.info("login successful")
            return
        } catch (e) {
            return loggAndReturnError('fetchToken: error: ' + e)
        }
    }
    return loggAndReturnError('fetchToken: action not found')
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
        const resp = await doFetch(request)
        if (resp) {
            const userId = resp.properties.userId
            if (userId) {
                logger.info("createUser: response successfully")
                const tokenAction = Siren.extractTokenAction(resp.actions)
                if (tokenAction) {
                    links.setTokenAction(tokenAction)
                    logger.info("createUser: setting up new token action: ", tokenAction.name)
                } else {
                    logger.error("createUser: token action not found in response")
                }
                return userId
            } else {
                logger.error("createUser: userId not found in response")
            }
        }
    }
    return loggAndReturnError('createUser: action not found')
}

function useFetchUserHome(): UserStats | Fetching | Error {
    const userHomeLink = links.getUserHomeLink()
    if (userHomeLink) {
        const request = {
            url: userHomeLink,
            method: "GET",
        }
        const resp = useFetchReducer(request)
        return handlerOrError(resp, (siren: Siren) => {
            logger.info("fetchUserHome: response successfully")
            const createGameAction = Siren.extractCreateGameAction(siren.actions)
            const getCurrentGameIdLink = Siren.extractGetCurrentGameIdLink(siren.links)
            const getGameLink = Siren.extractGetGameLink(siren.links)

            if (createGameAction) {
                links.setCreateGameAction(createGameAction)
                logger.info("fetchUserHome: setting up new create game action: ", createGameAction.name)
            } else {
                return loggAndReturnError('fetchUserHome: create game action not found in response')
            }
            if (getCurrentGameIdLink) {
                links.setCurrentGameIdLink(getCurrentGameIdLink)
                logger.info("fetchUserHome: setting up new get_current_game_id link: ", getCurrentGameIdLink)
            } else {
                return loggAndReturnError('fetchUserHome: get_current_game_id link not found in response')
            }
            if (getGameLink) {
                links.setGetGameLink(getGameLink)
                logger.info("fetchUserHome: setting up new get_game link: ", getGameLink)
            } else {
                return loggAndReturnError('fetchUserHome: get_game link not found in response')
            }
            return siren.properties
        })
    }
    return loggAndReturnError('fetchUserHome: link not found')
}

async function createGame(request: CreateGameRequest | undefined): Promise<CreateGameResponse | Error> {
    const action = links.getCreateGameAction()
    if (!action) {
        logger.error("createGame: create game action not found")
        return new Error("create game action undefined")
    }
    // TODO request could be undefined
    if (Siren.validateFields(request, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody(request),
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
                    logger.error("createGame: create game response not found")
                }
            }
        } catch (e) {
            return loggAndReturnError('createGame: error: ' + e)
        }
    }
    return loggAndReturnError('createGame: fields not valid')
}

async function getCurrentGameId(): Promise<number | Error> {
    const currentGameIdLink = links.getCurrentGameIdLink()
    if (!currentGameIdLink)
        return loggAndReturnError('getCurrentGameId: link not found')
    try {
        const response = await Fetch.doFetch({ url: currentGameIdLink, method: "GET", body: undefined })
        if (response) {
            logger.info("getGame: response successful")
            const gameId = response.properties.id
            if (gameId) {
                return gameId
            } else {
                logger.error("getGame: game id not found in response")
            }
        }
    } catch (e) {
        return loggAndReturnError('getGame: error: ' + e)
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
            logger.info("getGame: setting up new place ships action: ", placeShipsAction.name)
        } else {
            logger.error("getGame: place ships action not found in response")
        }
        if (confirmFleetAction) {
            links.setConfirmFleetAction(confirmFleetAction)
            logger.info("getGame: setting up new confirm fleet action: ", confirmFleetAction.name)
        } else {
            logger.error("getGame: confirm fleet action not found in response")
        }
        if (attackAction) {
            links.setAttackAction(attackAction)
            logger.info("getGame: setting up new attack action: ", attackAction.name)
        } else {
            logger.error("getGame: attack action not found in response")
        }
        if (quitGameAction) {
            links.setQuitGameAction(quitGameAction)
            logger.info("getGame: setting up new quit game action: ", quitGameAction.name)
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
            props.myPlayer,
            props.winner,
            props.playerTurn,
        )
    }


    const gameLink = links.getGameLink()
    if (!gameLink)
        return loggAndReturnError('getGame: game link not found')
    try {
        const response = await Fetch.doFetch({ url: gameLink, method: "GET", body: undefined })
        if (response) {
            const error = checkLinks(response) // returns a string if there is an error
            if (error)
                return new Error(error)
            logger.info("getGame: response successful")
            return fromSirenPropsToMatch(response.properties)
        } else {
            return loggAndReturnError('getGame: response undefined')
        }
    } catch (e) {
        return loggAndReturnError('getGame: error: ' + e)
    }
}


// TODO -> fails when parsing placeShipsRequest to JSON (but server doesnt responds well)
async function placeShips(placeShipsRequest: PlaceShipsRequest): Promise<void | Error> {
    const action = links.getPlaceShipsAction()
    if (!action) {
        logger.error("placeShips: token or place ships action undefined")
        return new Error("token or place ships action undefined")
    }
    if (Siren.validateFields(placeShipsRequest, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody(placeShipsRequest),
        }
        try {
            const siren = await doFetch(internalReq)
            if (siren) {
                logger.info("placeShips: response successful")
                return
            }
        } catch (e) {
            return loggAndReturnError('placeShips: error: ' + e)
        }
    }
    return loggAndReturnError('placeShips: fields not valid')
}
async function confirmFleet(): Promise<void | Error> {
    const action = links.getConfirmFleetAction()
    if (!action)
        return loggAndReturnError('confirmFleet: token or confirm fleet action undefined')

    const request = {fleetConfirmed: true} // request is set here because it is always the same
    if (Siren.validateFields(request, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody({fleetConfirmed: true}),
        }
        try {
            const siren = await doFetch(internalReq)
            if (siren) {
                logger.info("confirmFleet: response successful")
                return
            }
        } catch (e) {
            return loggAndReturnError('confirmFleet: error: ' + e)
        }
    }
    return loggAndReturnError('confirmFleet: fields not valid')
}

async function attack(attackRequest: any): Promise<void | Error> {
    const action = links.getAttackAction()
    if (!action) {
        logger.error("attack: token or attack action undefined")
        return new Error("token or attack action undefined")
    }
    if (Siren.validateFields(attackRequest, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody(attackRequest),
        }
        try {
            const siren = await doFetch(internalReq)
            if (siren) {
                logger.info("attack: response successful")
                return
            } else {
                return loggAndReturnError('attack: response undefined')
            }
        } catch (e) {
            return loggAndReturnError('attack: error: ' + e)
        }
    }
    return loggAndReturnError('attack: fields not valid')
}

async function quitGame(gameId: number): Promise<void | Error> {
    const action = links.getQuitGameAction()
    if (!action)
        return loggAndReturnError('quitGame: token or quit game action undefined')

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
            const siren = await doFetch(internalReq)
            if (siren) {
                logger.info("quitGame: response successful")
                return
            } else {
                return loggAndReturnError('quitGame: response undefined')
            }
        } catch (e) {
            return loggAndReturnError('quitGame: error: ' + e)
        }
    }
}


export class Fetching {}
function handlerOrError(state: State, handler: (siren: Siren) => any): any | Error | Fetching {
    switch (state.type) {
        case 'response' : {
            return handler(state.response)
        }
        case "fetching" : {
            return new Fetching()
        }
        case "error" : {
            return Error(state.message)
        }
        case 'started' : {
            return new Fetching()
        }
    }
}

function loggAndReturnError(error: string): Error {
    logger.error(error)
    return new Error(error)
}

export const Services = {
    useFetchHome,
    fetchHome,
    useFetchServerInfo,
    fetchBattleshipRanks: useFetchBattleshipRanks,
    getUser,
    fetchToken: doLogin,
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
