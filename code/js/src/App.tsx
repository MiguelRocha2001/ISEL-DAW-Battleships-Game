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

/*
function Home() {
    return (
        <div>
            <h1>Home</h1>
            <ol>
                <li><a href="/path1">screen 1 (using a)</a></li>
                <li><Link to="/path1">screen 1</Link></li>
                <li><Link to="/path2">screen 2</Link></li>
                <li><Link to="/path3">screen 3</Link></li>
                <li><Link to="/users/123">User 123</Link></li>
                <li><Link to="/users/abc">User abc</Link></li>
                <li><Link to="/users/123/games/xyz">User 123 Game xyz</Link></li>
            </ol>
        </div>
    )
}
*/


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
 