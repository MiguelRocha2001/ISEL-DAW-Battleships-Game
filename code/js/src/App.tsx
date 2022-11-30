import * as React from 'react'
import {
    createBrowserRouter, Link, RouterProvider, useParams,
} from 'react-router-dom'
import { Info } from './screens/Info'

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
        "path": "/path2",
        "element": <Rankings />
    },
    {
        "path": "/path3",
        "element": <Auth />
    },
    {
        "path": "/users/:uid",
        "element": <UserHome />
    },
    {
        "path": "/users/:uid/games/:gid",
        "element": <Game />
    }
])

export function App() {
    return (
        <RouterProvider router={router} />
    )
}

function Home() {
    return (
        <div>
            <h1>Home</h1>
            <ol>
                <li><a href="/info">Info</a></li>
                <li><Link to="/rankings">Rankings</Link></li>
                <li><Link to="/auth">Authentication</Link></li>
            </ol>
        </div>
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
 