export class NetworkError extends Error {
    constructor(message: string) {
        super(message)
        this.name = "NetworkError"
    }
}

export class ServerError extends Error {
    constructor(message: string) {
        super(message)
        this.name = "ServerError"
    }
}