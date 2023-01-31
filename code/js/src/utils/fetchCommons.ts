import {links} from "../server_info/links";
import {Body, KeyValuePair} from "./useFetch-reducer";
import {Siren} from "./siren";

const CONTENT_TYPE_JSON = 'application/json'

export type Request = {
    url: string
    method: string
    body?: Body,
}

export async function fetchRequest(request: Request): Promise<Response> {
    return await fetch(links.host + request.url, {
        method: request.method,
        body: request.body ? buildBody(request.body) : undefined,
        headers: {
            'Content-Type': CONTENT_TYPE_JSON,
        },
        credentials: 'include'
    })
}

export class ProblemJson {
    title: string
    status: number
    detail: string

    constructor(title: string, status: number, detail: string) {
        this.title = title
        this.status = status
        this.detail = detail
    }
}

export async function getSirenOrProblemOrUndefined(response: Response): Promise<Siren | ProblemJson | undefined> {
    if (response.ok) {
        const isSiren = response.headers.get('content-type')?.includes('application/vnd.siren+json');
        return isSiren ? await response.json() : null;
    } else {
        const problemJson = await response.json()
        return new ProblemJson(problemJson.title, response.status, problemJson.detail)
    }
}

function buildBody(fields: KeyValuePair[]): string {
    const body = {}
    fields.forEach(field => {
        body[field.name] = field.value
    })
    return JSON.stringify(body)
}