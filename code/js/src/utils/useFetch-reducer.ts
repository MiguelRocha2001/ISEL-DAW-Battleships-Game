import {useEffect, useReducer,} from 'react'
import {links} from "../server_info/links";
import {Logger} from "tslog";
import {Siren} from "./siren";
import {fetchRequest, getSirenOrProblemOrUndefined, ProblemJson} from "./fetchCommons";

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
        type : "networkError",
        error : string
    }
    |
    {
        type : "serverError",
        error : string,
        status : number
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
        type : "setResponse",
        response : Siren
    }
    |
    {
        type : "setNetworkError",
        message : string
    }
    |
    {
        type : "setServerError",
        message : string,
        status : number
    }

function reducer(state:State, action:Action): State {
    switch(action.type){
        case 'startFetch' : return {type : 'fetching'}
        case 'setResponse' : return {type : 'response' , response : action.response}
        case 'setNetworkError' : return {type : 'networkError' , error : action.message}
        case 'setServerError' : return {type : 'serverError' , error : action.message , status : action.status}
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
                    const response = await fetchRequest(request)
                    if (cancelled) return

                    const data = await getSirenOrProblemOrUndefined(response)
                    if (!cancelled) {
                        if (data instanceof ProblemJson) {
                            logger.error("Response Error: ", data.title)
                            dispatcher({type:'setServerError', message: data.title, status: data.status})
                        }
                        else
                            dispatcher({type : 'setResponse', response: data})
                    }
                } catch (error) {
                    logger.error("Network Error: ", error)
                    if (cancelled) return
                    dispatcher({type:'setNetworkError', message:error.message})
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