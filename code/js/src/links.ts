const host = 'http://localhost:8080/api/'

const defaultUrl = host

let infoLink: string | undefined

function getInfoLink() {
    return infoLink
}

function setInfoLink(link: string) {
    infoLink = link
}

export const links = {
    host,
    defaultUrl,
    getInfoLink,
    setInfoLink,
}