import * as React from 'react'
import {createRoot} from 'react-dom/client'
import {App} from './App'
import './../css/bootstrap.min.css'
import './../css/mdb.min.css'
import 'jquery'
import 'bootstrap'

const root = createRoot(document.getElementById("the-div"))

root.render(<App />)