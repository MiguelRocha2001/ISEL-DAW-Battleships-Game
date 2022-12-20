import * as React from 'react'
import {Services} from '../services'
import {Link} from 'react-router-dom'
import {ShowSirenProperties} from "../utils/ShowSirenProperties";

export function Me() {
    const response = Services.fetchUserHome()

    if (typeof response === "string") {
        return (
            <div>
                <ShowSirenProperties content={response} properties={[]} />
            </div>
        )
    } else {
        return (
            <div>
                <ShowSirenProperties content={response} properties={['userId', 'username']} />
                <li><Link to="/game">Game Screen</Link></li>
            </div>
        )
    }
}