import * as React from 'react'
import {
    createBrowserRouter, Link, RouterProvider, useParams,
} from 'react-router-dom'
import { Info } from './screens/Info'
import { Home } from './screens/Home'

const router = createBrowserRouter([
    {
        "path": "/",
        "element": <Home />
    },
    {
        "path": "/info",
        "element": <Info />
    },
    {
        "path": "/rankings",
        "element": <Rankings />
    }
])

export function App() {
    return (
        <RouterProvider router={router} />
    )
}


function Rankings() {
    return (
        <div>
            <h1>Screen 2</h1>
        </div>
    )
}

function Auth() {
    return (
        <div>
            <h1>Screen 3</h1>
        </div>
    )
}

function UserHome() {
    const {uid} = useParams()
    return (
        <div>
            <h2>User Detail</h2>
            {uid}
        </div>
    )
}

function Game() {
    const {gid, uid} = useParams()
    return (
        <div>
            <h2>User Game Detail</h2>
            {uid}, {gid}
        </div>
    )
}
 