import {useEffect, useReducer,} from 'react'
import {links} from "../server_info/links";
import {Logger} from "tslog";
import {Siren} from "./siren";

const logger = new Logger({ name: "useFetchReducer" });

const CONTENT_TYPE_JSON = 'application/json'

export type State =
    {
        type : "started",
    }
    |
    {
        type : "fetching",
    }
    |
    {
        type : "error",
        message : string
    }
    |
    {
        type : "response",
        response : Siren
    }

type Action =
    {
        type : "startFetch",
    }
    |
    {
        type : "setError",
        message : string
    }
    |
    {
        type : "setResponse",
        response : Siren
    }


function reducer(state:State, action:Action): State {
    switch(action.type){
        case 'startFetch' : return {type : 'fetching'}
        case 'setError' : return {type : 'error' , message : action.message}
        case 'setResponse' : return {type : 'response' , response : action.response}
    }
}

export type Request = {
    url: string
    method: string
    body?: Body,
}

export type Body = KeyValuePair[]

export type KeyValuePair = {
    name: string,
    value: string
}

export function useFetchReducer(request: Request) : State {
    const [state, dispatcher] = useReducer(reducer, {type : "started"})

    useEffect(() =>{
        let cancelled = false
        async function doFetch() {
            dispatcher({type : 'startFetch'})
            if (request && validateRequestMethod(request)) {
                logger.info("sending request to: ", links.host + request.url)
                // console.log("body: ", request.body ? buildBody(request.body) : undefined)
                try {
                    const response = await fetch(links.host + request.url, {
                        method: request.method,
                        body: request.body ? buildBody(request.body) : undefined,
                        headers: {
                            'Content-Type': CONTENT_TYPE_JSON,
                        },
                        credentials: 'include'
                    })
                    if (cancelled) return
                    const body = await response.json()
                    if (!cancelled) {
                        if (response.status >= 300) {
                            logger.error("Response Error: ", response.status)
                            dispatcher({type:'setError', message: body})
                        }
                        dispatcher({type : 'setResponse', response: body})
                    }
                    return body
                } catch (error) {
                    logger.error("Network Error: ", error)
                    if (cancelled) return
                    dispatcher({type:'setError', message:error.message})
                }
            }
        }
        
        doFetch()
        return ()=>{
            cancelled = true
        }
        
    }, [request.url, request.method, request.body])
    return state
}


function validateRequestMethod(request: Request): boolean {
    const method = request.method.toUpperCase()
    return request.url && (method === 'GET' || method === 'POST' || method === 'PUT' || method === 'DELETE')
}

function buildBody(fields: KeyValuePair[]): string {
    const body = {}
    fields.forEach(field => {
        body[field.name] = field.value
    })
    return JSON.stringify(body)
}