import * as React from 'react'
import { ShowSirenProperties } from '../utils/ShowSirenProperties'
import { Services } from '../services'

export function Info() {
    const content = Services.useFetchServerInfo()
    console.log(content)
    return (
        <div>
            <ShowSirenProperties content={content} properties={['authors', 'systemVersion']} />
        </div >
    )
}