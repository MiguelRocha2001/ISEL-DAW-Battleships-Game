import * as React from 'react'
import {createRoot} from 'react-dom/client'
import {App} from './App'
import 'jquery'
import {Services} from './services'

console.log("Hello from index.tsx")

const root = createRoot(document.getElementById("the-div"))

Services.fetchHome().then(()=> root.render(<App />)) // Fetch the home resource

