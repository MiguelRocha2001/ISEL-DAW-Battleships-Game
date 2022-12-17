import * as React from 'react'
import {
    useEffect,
} from 'react'
import { Services } from '../services'
import { Link } from 'react-router-dom'

type State = 
    {
        type : "loading",
    }
    |
    {
        type : "game screen"
    }
    |
    {
        type : "error",
        msg : string
    }

type Action =     
    {
        type : "setLoading"
    }
    |
    {
        type : "setGameScreen"
    }
    |
    {
        type : "setError",
        msg : string
    }

function reducer(state: State, action: Action): State {
    switch(action.type) {
        case 'setLoading' : {
            return {type : 'loading'}
        }
        case 'setGameScreen' : {
            return {type : 'game screen'}
        }
        case 'setError' : {
            return {type : 'error', msg : action.msg}
        }
    }
}

export function Me() {
    const [state, dispatch] = React.useReducer(reducer, {type : 'loading'}) 

    useEffect(() => {
        async function fetchUserHome() {
            const content = await Services.fetchUserHome()
            if (typeof content == "string") {
                dispatch({type : "setError", msg : content})
            } else {
                dispatch({type : "setGameScreen"})
            }
        }
        if (state.type == "loading")
            fetchUserHome()
    }, [state])

    switch(state.type) {
        case 'loading' : {
            return (
                <div> Loading... </div>
            )
        }
        case 'game screen' : {
            return (
                <div>
                    <li><Link to="/game">Game Screen</Link></li>
                </div>
            )
        }
        case 'error' : {
            return (
                <div>
                    <p> {state.msg} </p>
                </div>
            )
        }
    }
}