import { links } from './links'
import { Siren } from './utils/siren'
import { Link } from './utils/siren'
import { useFetch } from './utils/useFetch'

function fetchHome(): Siren | undefined {
    const defaultUrl = links.defaultUrl
    const resp = useFetch(defaultUrl)
    if (resp) {
        const link = extractInfoLink(resp.links)
        console.log("Setting info link to " + link)
        links.setInfoLink(link)
    }
    return resp
}

function extractInfoLink(linksArg: Link[]): string {
    for (let i = 0; i < linksArg.length; i++) {
        const link = linksArg[i]
        for (let j = 0; j < link.rel.length; j++) {
            if (link.rel[j] === "server-info") {
                console.log("Found server-info link: " + links.host + link.href)
                return links.host + link.href
            }
        }
    }
    return undefined
}

function fetchServerInfo(): Siren | undefined {
    const infoLink = links.getInfoLink()
    if (infoLink) {
        return useFetch(infoLink)
    }
    const resp = fetchHome()
    if (resp) {
        const link = extractInfoLink(resp.links)
        return useFetch(link)
    }
    return undefined
}

export const navigation = {
    fetchHome,
    fetchServerInfo,
}