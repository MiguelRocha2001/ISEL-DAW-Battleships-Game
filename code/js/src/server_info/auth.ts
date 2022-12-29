import {useEffect, useReducer, useState} from "react";

export type State =
    {
        type : "logged",
        token : string
    }
    |
    {
        type : "notLogged"
    }

type Action =
    {
        type : "logIn",
        token : string
    }
    |
    {
        type : "logOut",
    }

function reducer(state:State, action: Action): State {
    switch(action.type) {
        case 'logIn' : return {type : 'logged', token : action.token}
        case 'logOut' : return {type : 'notLogged'}
    }
}

let state
let dispatcher

function useAuthentication(token: string): string | undefined {
    [state, dispatcher] = useReducer(reducer, {type : "notLogged"})

    useEffect(() => {
        if (token) {
            dispatcher({type : 'logIn', token})
        } else {
            dispatcher({type : 'logOut'})
        }
    }, [token])

    return state.type === "logged" ? state.token : undefined
}

function getToken() {
    if (state && dispatcher) {
        return state.type === "logged" ? state.token : undefined
    }
    return undefined
}

function setToken(tokenArg: string) {
    if (state && dispatcher) {
        dispatcher({type: 'logIn', token: tokenArg})
    }
}

export const auth = {
    useAuthentication,
    getToken,
    setToken,
}
