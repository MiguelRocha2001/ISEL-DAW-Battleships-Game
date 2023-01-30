import * as React from 'react'
import {BrowserRouter, Outlet, Route, Routes} from 'react-router-dom'
import {Info} from './screens/Info'
import {Home} from './screens/Home'
import {Leaderboard} from './screens/Leaderboard'
import {AuthnContainer} from './screens/auth/Authn'
import {Me} from './screens/Me'
import {Game} from './screens/Game'
import style from "../static-files/css/battleships/commons.css";
import {User} from "./screens/User";
import {Authentication} from "./screens/auth/Authentication";
import {Navbar} from "./NavBar";
import {PageNotFound} from "./screens/auth/PageNotFound";
import {RequireAuthn} from "./screens/auth/RequireAuthn";

export function App() {
    return (
        <div className={style}>
            <AuthnContainer><Outlet />
                <BrowserRouter >
                    <Navbar />
                    <div className={"content"}>
                        <Routes>
                            <Route path='/' element={<Home />} />
                            <Route path='/info' element={<Info />} />
                            <Route path='/leadership/:page' element={<Leaderboard />} />
                            <Route path='/users/:id' element={<User />} />
                            <Route path='/sign-in' element={<Authentication title={'Sign in'} action={'login'}/>} />
                            <Route path='/sign-up' element={<Authentication title={'Sign Up'} action={'register'}/>} />
                            <Route path='me' element={<RequireAuthn children={<Me />} />} />
                            <Route path='game' element={<RequireAuthn children={<Game/>} />} />
                            <Route path='*' element={<PageNotFound />} />
                        </Routes>
                    </div>
                </BrowserRouter>
            </AuthnContainer>
        </div>
    );
}