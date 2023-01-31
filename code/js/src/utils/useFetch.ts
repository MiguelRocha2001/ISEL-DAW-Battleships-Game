import {Siren} from './siren'
import {links} from '../server_info/links'
import {Logger} from "tslog";
import {NetworkError, ServerError} from "./domain";
import {fetchRequest, getSirenOrProblemOrUndefined, ProblemJson} from "./fetchCommons";

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

export async function doFetch(request: Request): Promise<Siren | undefined | ServerError> {
    if (request && validateRequestMethod(request)) {
        logger.info("sending request to: ", links.host + request.url)
        // console.log("body: ", request.body ? buildBody(request.body) : undefined)
        try {
            const resp = await fetchRequest(request)
            const data = await getSirenOrProblemOrUndefined(resp)

            if (data instanceof ProblemJson) {
                logger.error("Response Error: ", data.title)
                return new ServerError(data.title, resp.status)
            }
            return data
        } catch (error) {
            logger.error("Network Error: ", error)
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