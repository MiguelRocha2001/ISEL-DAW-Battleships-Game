import * as React from 'react'
import {
    BrowserRouter,
    createBrowserRouter,
    Link,
    Navigate,
    Outlet,
    Route,
    Router,
    RouterProvider,
    Routes
} from 'react-router-dom'
import {Info} from './screens/Info'
import {Home} from './screens/Home'
import {Leaderboard} from './screens/Leaderboard'
import {AuthnContainer} from './screens/auth/Authn'
import {Me} from './screens/Me'
import {Game} from './screens/Game'
import style from "../static-files/css/battleships/commons.css";
import {LogInfo} from "./LogInfo";
import {User} from "./screens/User";
import {Authentication} from "./screens/auth/Authentication";
import {Navbar} from "./NavBar";
import {PageNotFound} from "./screens/auth/PageNotFound";
import {useState} from "react";

export function App() {

    const [activeIndex, setActiveIndex] = useState(0);

    return (
        <div className={style}>
            <BrowserRouter >
                <Navbar isActive={activeIndex}/>
                <div className={"content"}>
                    <Routes>
                        <Route path='/' element={<AuthnContainer><Outlet /></AuthnContainer>}>
                            <Route path='/' element={<Home />} />
                            <Route path='/info' element={<Info />} />
                            <Route path='/leadership' element={<Leaderboard />} />
                            <Route path='/users/:id' element={<User />} />
                            <Route path='/sign-in' element={<Authentication title={'Sign in'} action={'login'}/>} />
                            <Route path='/sign-up' element={<Authentication title={'Sign Up'} action={'register'}/>} />
                            <Route path='/me' element={<Me />} />
                            <Route path='/game' element={<Game />} />
                            <Route path='*' element={<PageNotFound />} />
                        </Route>
                    </Routes>
                </div>
            </BrowserRouter>
            <LogInfo onShow={() => setActiveIndex(1)} stopOnShow={() => setActiveIndex(0)} />
        </div>
    );
}