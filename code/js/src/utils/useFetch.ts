import {
    useState,
    useEffect,
} from 'react'
import { Siren } from './siren'

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

export async function doFetch(request: Request): Promise<Siren | undefined> {
    if (validateRequestMethod(request.method)) {
        const resp = await fetch(request.url, {
            method: request.method,
            body: JSON.stringify(request.body)
        })
        const body = await resp.json()
        return body
    } else {
        throw new Error("Invalid request method")
    }
}