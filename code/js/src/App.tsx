import * as React from 'react'
import {
    createBrowserRouter, Link, RouterProvider, useParams,
} from 'react-router-dom'
import { Info } from './screens/Info'
import { Home } from './screens/Home'
import { Rankings } from './screens/Rankings'
import { Auth } from './screens/auth/Auth'

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
    },
    {
        "path": "/auth",
        "element": <Auth />
    }
])

export function Router() {
    return (
        <RouterProvider router={router} />
    )
}