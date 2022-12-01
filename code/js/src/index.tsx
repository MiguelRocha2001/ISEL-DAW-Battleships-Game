import * as React from 'react'
import { createRoot } from 'react-dom/client'
import { Router } from './App'	
import { TopBar } from './AppBar'

const root = createRoot(document.getElementById("the-div"))

root.render(<App />)

/**
 * Launches Top Bar and Router.
 * @returns 
 */
function App() {
    return (
        <div>
            <TopBar />
            <Router />
        </div>
    )
}

