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

/*
export function useFetch(request: Request): Siren | undefined {
    const [content, setContent] = useState(undefined)
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            if (validateRequestMethod(request.method)) {
                const resp = await fetch(request.url, {
                    method: request.method,
                    body: JSON.stringify(request.body)
                })
                const body = await resp.json()
                if (!cancelled) {
                    setContent(body)
                }
            } else {
                throw new Error("Invalid request method")
            }
        }
        setContent(undefined)
        doFetch()
        return () => {
            cancelled = true
        }
    }, [request.url])
    return content
}
*/

function validateRequestMethod(method: string) {
    return method === 'GET' || method === 'POST' || method === 'PUT' || method === 'DELETE'
}

// TODO -> receives 415 Unsupported Media Type on POST requests
export async function doFetch(request: Request): Promise<Siren | undefined> {
    if (validateRequestMethod(request.method)) {
        logger.info("senfing request to: ", links.host + request.url)
        const resp = await fetch(links.host + request.url, {
            method: request.method,
            body: request.body ? buildBody(request.body) : undefined,
            headers: {
                'Content-Type': 'application/json'
            },
        })
        console.log("response: ", resp)
        return await resp.json()
    } else {
        throw new Error("Invalid request method")
    }
}

function buildBody(fields: ActionInput[]): string {
    const body = {}
    fields.forEach(field => {
        body[field.name] = field.value
    })
    return JSON.stringify(body)
}