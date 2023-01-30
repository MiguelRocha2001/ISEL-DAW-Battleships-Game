import {Siren} from './siren'
import {links} from '../server_info/links'
import {Logger} from "tslog";
import {NetworkError, ServerError} from "./domain";

const logger = new Logger({ name: "useFetch" });

const CONTENT_TYPE_JSON = 'application/json'

export type Request = {
    url: string
    method: string
    body?: Body
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

export async function doFetch(request: Request): Promise<Siren | ServerError> {
    if (request && validateRequestMethod(request)) {
        logger.info("sending request to: ", links.host + request.url)
        // console.log("body: ", request.body ? buildBody(request.body) : undefined)
        try {
            const resp = await fetch(links.host + request.url, {
                method: request.method,
                body: request.body ? buildBody(request.body) : undefined,
                headers: {
                    'Content-Type': CONTENT_TYPE_JSON,
                },
                credentials: "include"
            })
            const body = await resp.json()
            if (resp.status >= 300) {
                logger.error("doFetch: ", resp.status)
                return new ServerError("No Info", resp.status)
            }
            return body
        } catch (error) {
            logger.error("doFetch: ", error)
            return Promise.reject(new NetworkError(error.message))
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
    toBody
}