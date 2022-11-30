import {
    useState,
    useEffect,
} from 'react'
import { Siren } from './siren'

export function 
useFetch(url: string): Siren | undefined {
    const [content, setContent] = useState(undefined)
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            try {
                const resp = await fetch(url)
                const body = await resp.json()
                
                if (!cancelled) {
                    setContent(body)
                }
            } catch (e) {
                console.log("error", e)
            }
        }
        setContent(undefined)
        doFetch()
        return () => {
            cancelled = true
        }
    }, [url])
    return content
}