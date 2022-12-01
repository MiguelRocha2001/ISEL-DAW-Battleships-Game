let token: string | undefined

function getToken() {
    return token
}

function setToken(tokenArg: string) {
    token = tokenArg
}

export const auth = {
    getToken,
    setToken,
}
