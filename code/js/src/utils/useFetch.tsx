import {
    useState,
    useEffect,
} from 'react'

type Siren = {
    class: string;
    properties: Object;
    links: Link[];
    entities: Entity[];
    actions: Action[];
  };

type Link = {
    rel: string[];
    href: string;
    title?: string;
    type?: string;
};

type Entity = {
    class: string[];
    properties: Object;
    entities: Entity[];
    links: Link[];
    actions: Action[];
    title: string;
};

type Action = {
    name: string;
    title: string;
    method: string;
    href: string;
    type: string;
    fields: Field[];
};

type Field = {
    name: string;
    type: string;
    value: string;
};

export function 
useFetch(url: string): string | undefined {
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