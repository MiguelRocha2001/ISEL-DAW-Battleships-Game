import * as React from 'react'
import {BrowserRouter, createBrowserRouter, Link, Outlet, Route, Router, RouterProvider, Routes} from 'react-router-dom'
import {Info} from './screens/Info'
import {Home} from './screens/Home'
import {Rankings} from './screens/Rankings'
import {AuthnContainer} from './screens/auth/Authn'
import {Me} from './screens/Me'
import {Game} from './screens/Game'
import {Authentication} from './screens/auth/Authentication'
import {auth} from "./server_info/auth";
import Navbar from "./NavBar";
import style from "../static-files/css/battleships/commons.css";

export function LogInfo() {
    const authenticated = auth.useAuthentication(undefined)
    return (
        <div>
            <p>{authenticated ? "Logged" : "NotLogged"}</p>
        </div>
    )
}

export function App() {
    return (
        <div className={style}>
            <BrowserRouter>
                <Navbar />
                <Routes>
                    <Route path='/' element={<AuthnContainer><Outlet /></AuthnContainer>}>
                        <Route path='/' element={<Home />} />
                        <Route path='/info' element={<Info />} />
                        <Route path='/rankings' element={<Rankings />} />
                        <Route path='/sign-in' element={<Authentication title={'Sign in'} action={'login'}/>} />
                        <Route path='/sign-up' element={<Authentication title={'Sign Up'} action={'register'}/>} />
                        <Route path='/me' element={<Me />} />
                        <Route path='/game' element={<Game />} />
                    </Route>
                </Routes>
            </BrowserRouter>
        </div>
    );
}