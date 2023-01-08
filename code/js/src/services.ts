import {links} from './server_info/links'
import {Action, Siren} from './utils/siren'
import {doFetch, Fetch, KeyValuePair} from './utils/useFetch'
import {Logger} from "tslog";
import {CreateGameRequest, CreateGameResponse, Game, PlaceShipsRequest} from './domain'
import {State, useFetchReducer} from "./utils/useFetch-reducer";

const logger = new Logger({ name: "Services" });


function useFetchHome(): any | string {
    const defaultUrl = links.defaultUrl
    const state = useFetchReducer({ url: defaultUrl, method: "GET" })
    return handlerOrError(state, (siren: Siren) => {
        logger.info("fetchHome: response successful")
        const serverInfoLink = Siren.extractInfoLink(siren.links)
        const battleshipRanksLink = Siren.extractBattleshipRanksLink(siren.links)
        const getUserLink = Siren.extractGetUserLink(siren.links)
        const tokenAction = Siren.extractTokenAction(siren.actions)
        const registerAction = Siren.extractRegisterAction(siren.actions)
        const createGameAction = Siren.extractCreateGameAction(siren.actions)
        const getCurrentGameIdLink = Siren.extractGetCurrentGameIdLink(siren.links)
        const getGameLink = Siren.extractGetGameLink(siren.links)
        const getQuitGameAction = Siren.extractQuitGameAction(siren.actions)
        if (serverInfoLink)
            logger.info("fetchHome: setting up new info endpoint: ", serverInfoLink)
        if (battleshipRanksLink)
            logger.info("fetchHome: setting up new battleship ranks endpoint: ", battleshipRanksLink)
        if (getUserLink)
            logger.info("fetchHome: setting up new get user endpoint: ", getUserLink)
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
        links.setTokenAction(tokenAction)
        links.setRegisterAction(registerAction)
        links.setCreateGameAction(createGameAction)
        links.setCurrentGameIdLink(getCurrentGameIdLink)
        links.setGetGameLink(getGameLink)
        links.setQuitGameAction(getQuitGameAction)
        return siren.properties
    })
}

export type ServerInfo = {
    authors: Array<Author>
    systemVersion: string
}
export type Author = {
    name: string
    email: string
}
function useFetchServerInfo(): ServerInfo | string {
    const infoLink = links.getInfoLink()
    const state = useFetchReducer({ url: infoLink, method: "GET" })
    return handlerOrError(state, (siren: Siren) => {
        logger.info("fetchServerInfo: response successfully")
        return siren.properties
    })
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
function useFetchBattleshipRanks(): Rankings | string {
    const ranksLink = links.getBattleshipRanksLink()
    if (ranksLink) {
        const state = useFetchReducer({url: ranksLink, method: "GET"})
        return handlerOrError(state, (siren: Siren) => {
            logger.info("fetchServerInfo: response successful")
            return siren.properties
        })
    }
    logger.error("useFetchBattleshipRanks: link not found")
    return 'Please, return to home page'
}

function getUser(userId: number): UserStats | string {
    const oldLink = links.getUserLink()
    const userLink = oldLink.replace(':id', userId.toString())
    if (userLink) {
        const state = useFetchReducer({url: userLink, method: "GET"})
        return handlerOrError(state, (siren: Siren) => {
            logger.info("fetchServerInfo: response successful")
            return siren.properties
        })
    }
    logger.error("getUser: link not found")
    return 'Please, return to home page'
}

async function fetchToken(fields: KeyValuePair[]): Promise<string | undefined> {
    const action = links.getTokenAction()
    if (action) {
        const request = action ? {
            url: action.href,
            method: action.method,
            body: fields
        } : undefined
        const resp = await doFetch(request)
        if (resp) {
            const token = resp.properties.token
            if (token) {
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
                logger.info("fetchToken: token set to: ", token)
                return token
            } else {
                logger.error("fetchToken: token not found in response")
                return undefined
            }
        }
    }
    logger.error("fetchToken: token action not found")
    return undefined
}

async function createUser(fields: KeyValuePair[]): Promise<string | undefined> {
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
                logger.error("createUser: token not found in response")
                return undefined
            }
        }
    }
    logger.error("createUser: register action not found")
    return undefined
}

function useFetchUserHome(user: string): UserStats | string {
    const userHomeLink = links.getUserHomeLink()
    if (user && userHomeLink) {
        const request = {
            url: userHomeLink,
            method: "GET",
            token: user
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
                logger.error("fetchUserHome: create game action not found in response")
                return "Couldn't find create game action"
            }
            if (getCurrentGameIdLink) {
                links.setCurrentGameIdLink(getCurrentGameIdLink)
                logger.info("fetchUserHome: setting up new get_current_game_id link: ", getCurrentGameIdLink)
            } else {
                logger.error("fetchUserHome: get_current_game_id link not found in response")
                return "Couldn't find get current game id link"
            }
            if (getGameLink) {
                links.setGetGameLink(getGameLink)
                logger.info("fetchUserHome: setting up new get_game link: ", getGameLink)
            } else {
                logger.error("fetchUserHome: get_game link not found in response")
                return "Couldn't find get game link"
            }
            return siren.properties
        })
    }
    return "Token at fault, or user home link not found"
}

async function createGame(request: CreateGameRequest | undefined, user: string): Promise<CreateGameResponse | string> {
    const action = links.getCreateGameAction()
    if (!user || !action) {
        logger.error("createGame: token or create game action undefined")
        return "createGame: token or create game action undefined"
    }
    // TODO request could be undefined
    if (Siren.validateFields(request, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody(request),
            token: user
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

async function getCurrentGameId(user: string): Promise<number | string> {
    const currentGameIdLink = links.getCurrentGameIdLink()
    if (!user || !currentGameIdLink) return 'Token or get-current-game-id link undefined'
    try {
        const response = await Fetch.doFetch({ url: currentGameIdLink, method: "GET", body: undefined, token: user })
        if (response) {
            logger.info("getGame: response successful")
            const gameId = response.properties.id
            if (gameId) {
                return gameId
            } else {
                logger.error("getCurrentGameId: gameId not found in response")
                return 'GameId not found in response'
            }
        }
    } catch (e) {
        logger.error("getCurrentGameId: error: ", e)
        return e.title
    }
}

async function getGame(user: string): Promise<Game | string> {

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
        if (quitGameAction) {
            links.setQuitGameAction(quitGameAction)
            logger.info("getGame: setting up new quit game action: ", quitGameAction.name)
        } else {
            logger.error("getGame: quit game action not found in response")
            return "Couldn't find quit game action"
        }
        return undefined
    }


    const gameLink = links.getGameLink()
    if (!user || !gameLink) return 'Token or get-game link not found'
    try {
        const response = await Fetch.doFetch({ url: gameLink, method: "GET", body: undefined, token: user })
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
async function placeShips(placeShipsRequest: PlaceShipsRequest, user: string): Promise<void | string> {
    const action = links.getPlaceShipsAction()
    if (!user || !action) {
        logger.error("placeShips: token or place ships action undefined")
        return "placeShips: token or place ships action undefined"
    }
    if (Siren.validateFields(placeShipsRequest, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody(placeShipsRequest),
            token: user
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
async function confirmFleet(user: string): Promise<void | string> {
    const action = links.getConfirmFleetAction()
    console.log('Action', action)
    if (!user || !action) {
        logger.error("confirmFleet: token or confirm fleet action undefined")
        return "confirmFleet: token or confirm fleet action undefined"
    }
    const request = {fleetConfirmed: true} // request is set here because it is always the same
    if (Siren.validateFields(request, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody({fleetConfirmed: true}),
            token: user
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

async function attack(attackRequest: any, user: string): Promise<void | string> {
    const action = links.getAttackAction()
    if (!user || !action) {
        logger.error("attack: token or attack action undefined")
        return "attack: token or attack action undefined"
    }
    if (Siren.validateFields(attackRequest, action)) {
        const internalReq = {
            url: action.href,
            method: action.method,
            body: Fetch.toBody(attackRequest),
            token: user
        }
        try {
            const siren = await doFetch(internalReq)
            if (siren) {
                logger.info("attack: response successful")
                return
            } else {
                logger.error("attack: bad response")
                return 'Received bad response'
            }
        } catch (e) {
            logger.error("attack: error: ", e.title)
            return e.title
        }
    }
    return 'attackRequest not valid'
}

async function quitGame(gameId: number, user: string): Promise<void | string> {
    const action = links.getQuitGameAction()
    if (!user || !action) {
        logger.error("quitGame: token or quit game action undefined")
        return "quitGame: token or quit game action undefined"
    }
    const template = action.href
    const href = template.replace(':id', gameId.toString())
    const request = {} // request is set here because it is always the same
    if (Siren.validateFields(request, action)) {
        const internalReq = {
            url: href,
            method: action.method,
            body: Fetch.toBody(request),
            token: user
        }
        try {
            const siren = await doFetch(internalReq)
            if (siren) {
                logger.info("quitGame: response successful")
                return
            } else {
                logger.error("quitGame: bad response")
                return 'Received bad response'
            }
        } catch (e) {
            logger.error("quitGame: error: ", e.title)
            return e.title
        }
    }
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
    getUser,
    fetchToken,
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
