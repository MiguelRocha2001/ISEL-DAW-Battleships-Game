import * as React from 'react'
import { Siren } from './siren'

/**
 * Shows the properties of a Siren object, or just the string, if obj is a string.
 * @param obj
 * @param properties
 * @constructor
 */
export function ShowSirenProperties({ content, properties }: { content: any | string, properties: string[] }) {
    if (typeof content == "string") {
        return (
            <div>{content}</div>
        )
    }
    const keys = Object.keys(content)
    const filteredKeys = keys
        .filter(key => properties.includes(key))
        .map(key =>
            <div key = {key}>
                <h1>{key.toUpperCase()}</h1>
                <p>{JSON.stringify(content[key])}</p>
            </div>
        )
    return (
        <div>
            {filteredKeys}
        </div>
    )
}