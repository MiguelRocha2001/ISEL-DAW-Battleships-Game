import * as React from 'react'
import {
    createBrowserRouter, Link, RouterProvider, useParams, Outlet
} from 'react-router-dom'
import { Info } from './screens/Info'
import { Home } from './screens/Home'
import { Rankings } from './screens/Rankings'
import { AuthnContainer } from './screens/auth/Authn'
import { Me } from './screens/Me'
import { Game } from './screens/Game'
import { Login } from './screens/auth/Login'


const router = createBrowserRouter([
    {
        "path": "/",
        "element": <AuthnContainer><Outlet /></AuthnContainer>,
        "children": [
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
            },
            {
                "path": "/auth",
                "element": <Login />
            },
            {
                "path": "/me",
                "element": <Me />,
            },
            {
                "path": "/game",
                "element": <Game />,
            }
        ]
    }
])

export function Router() {
    return (
        <RouterProvider router={router} />
    )
}