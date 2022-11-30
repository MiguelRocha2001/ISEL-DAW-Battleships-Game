import * as React from 'react'
import { Siren } from './siren'

export function Show({ content, property }: { content: Siren, property?: string }) {
    if (!content) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    return (
        <div>
            <pre>
                {JSON.stringify(content.properties[property], null, 2)}
            </pre>
        </div>
    )
}