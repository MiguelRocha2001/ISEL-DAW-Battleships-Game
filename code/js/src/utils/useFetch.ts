import {
    useState,
    useEffect,
} from 'react'
import { Siren } from './siren'
import { links } from '../server_info/links'
import { Logger } from "tslog";

const logger = new Logger({ name: "useFetch" });

const CONTENT_TYPE_JSON = 'application/json'

export type Request = {
    url: string
    method: string
    body?: Body,
    token?: string
}

export type Body = KeyValuePair[]

export type KeyValuePair = {
    name: string,
    value: string
}

function toBody(obj: any): Body {
    const body: Body = []
    for (const key in obj) {
        body.push({ name: key, value: obj[key] })
    }
    return body
}

function validateRequestMethod(request: Request): boolean {
    const method = request.method.toUpperCase()
    return request.url && (method === 'GET' || method === 'POST' || method === 'PUT' || method === 'DELETE')
}

// TODO -> receives 415 Unsupported Media Type on POST requests
export function useFetch(request: Request): Siren | undefined {
    const [content, setContent] = useState(undefined)
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            if (request && validateRequestMethod(request)) {
                logger.info("sending request to: ", links.host + request.url)
                const resp = await fetch(links.host + request.url, {
                    method: request.method,
                    body: request.body ? buildBody(request.body) : undefined,
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + request.token
                    },
                })
                const body = await resp.json()
                console.log(body)
                if (!cancelled) {
                    setContent(body)
                }
            }
        }
        setContent(undefined)
        doFetch()
        return () => {
            cancelled = true
        }
    }, [request.url, request.method, request.body])
    return content
}

// TODO -> receives 415 Unsupported Media Type on POST requests
export async function doFetch(request: Request): Promise<Siren> {
    if (request && validateRequestMethod(request)) {
        logger.info("sending request to: ", links.host + request.url)
        // console.log("body: ", request.body ? buildBody(request.body) : undefined)
        try {
            const resp = await fetch(links.host + request.url, {
                method: request.method,
                body: request.body ? buildBody(request.body) : undefined,
                headers: request.token ? {
                    'Content-Type': CONTENT_TYPE_JSON,
                     'Authorization': 'Bearer ' + request.token 
                    } : {
                    'Content-Type': CONTENT_TYPE_JSON
                    }
            })
            const body = await resp.json()
            if (resp.status >= 300) {
                logger.error("doFetch: ", resp.status)
                return Promise.reject(body)
            }
            return body
        } catch (error) {
            logger.error("doFetch: ", error)
            return Promise.reject(error)
        }
    }
}

function buildBody(fields: KeyValuePair[]): string {
    const body = {}
    fields.forEach(field => {
        body[field.name] = field.value
    })
    return JSON.stringify(body)
}

export const Fetch = {
    doFetch,
    useFetch,
    toBody
}