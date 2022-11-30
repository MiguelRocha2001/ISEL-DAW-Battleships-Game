import * as React from 'react'
import {
    createBrowserRouter, Link, RouterProvider, useParams,
} from 'react-router-dom'
import { Info } from './screens/Info'
import { Home } from './screens/Home'
import { AuthnContainer } from './screens/auth/Authn'
import { useLoggedIn, useSetLogin } from './screens/auth/Authn'
import { Rankings } from './screens/Rankings'

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