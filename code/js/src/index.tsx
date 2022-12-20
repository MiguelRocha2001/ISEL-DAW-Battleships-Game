import * as React from 'react'
import {createRoot} from 'react-dom/client'
import {Router} from './App'
import {TopBar} from './AppBar'
import './../css/bootstrap.min.css'
import './../css/mdb.min.css'
import 'jquery'
import 'bootstrap'

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

