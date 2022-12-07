import * as React from 'react'
import { Link } from 'react-router-dom'

export function Me() {
    return (
        <div>
            <ul>
                <li><Link to="/game">Start Game</Link></li>
            </ul>
        </div>
    )
}