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
        type : "start Game"
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
        type : "setStartGame"
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
        case 'setStartGame' : {
            return {type : 'start Game'}
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
                dispatch({type : "setStartGame"})
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
        case 'start Game' : {
            return (
                <div>
                    <li><Link to="/game">Start Game</Link></li>
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