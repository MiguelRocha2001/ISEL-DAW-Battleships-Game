import * as React from 'react'
import {Services} from '../services'
import {Link} from 'react-router-dom'
import {ShowSirenProperties} from "../utils/ShowSirenProperties";

export function Me() {
    const response = Services.fetchUserHome()
    console.log(response)
    if (typeof response === "string") {
        return (
            <p>{response}</p>
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