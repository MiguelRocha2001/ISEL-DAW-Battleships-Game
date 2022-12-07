import {
    useState,
    useEffect,
} from 'react'
import { Siren } from './siren'
import { links } from '../server_info/links'
import { Logger } from "tslog";

const logger = new Logger({ name: "useFetch" });

export type Request = {
    url: string
    method: string
    body?: ActionInput[]
}

export type ActionInput = {
    name: string,
    value: string
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
                        'Content-Type': 'application/json'
                    },
                })
                const body = await resp.json()
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


    
function buildBody(fields: ActionInput[]): string {
    const body = {}
    fields.forEach(field => {
        body[field.name] = field.value
    })
    return JSON.stringify(body)
}