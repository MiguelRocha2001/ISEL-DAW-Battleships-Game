import * as React from 'react'
import { Link } from 'react-router-dom'
import { navigation } from './navigation'

export function TopBar() {
    const isLogged = navigation.isLogged()
    return (
        <div>
            <p>{isLogged ? "Logged" : "NotLogged"}</p>
        </div>
    )
}