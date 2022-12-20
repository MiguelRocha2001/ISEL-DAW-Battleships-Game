import * as React from 'react'
import {Services} from './services'

export function TopBar() {
    const isLogged = Services.isLogged()
    return (
        <div>
            <p>{isLogged ? "Logged" : "NotLogged"}</p>
        </div>
    )
}