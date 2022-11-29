import {
    useState,
    useEffect,
} from 'react'

export function 
useFetch(url: string): string | undefined {
    const [content, setContent] = useState(undefined)
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            try {
                const resp = await fetch(url)
                console.log("resp", resp)
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