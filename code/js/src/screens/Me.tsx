import * as React from 'react'
import { Link } from 'react-router-dom'
import { Services } from '../services'

export function Me() {
    const content = Services.fetchUserHome()

    if (content) {
    return (
        <div>
            <ul>
                <li><Link to="/game">Start Game</Link></li>
            </ul>
        </div>
    )
    } else {
        return (
            <div>
                <p> Loading... </p>
            </div>
        )
    }
}