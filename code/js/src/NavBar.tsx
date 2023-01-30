import * as React from 'react'
import {Bars, Nav, NavBtn, NavBtnLink, NavLink, NavMenu,} from './NavbarElements';
import {useCurrentUser, useSetUser} from "./screens/auth/Authn";
import style from "./LogoutButton.module.css";

export function Navbar () {
    const currentUser = useCurrentUser()
    const setUser = useSetUser()

    const profile = (
        <NavLink style={{display: currentUser ? undefined : "none"}} to='/me' activestyle="true">
            Profile
        </NavLink>
    )

    const play = (
        <NavLink to='/game' style={{display: currentUser ? undefined : "none"}} activestyle="true">
            Play
        </NavLink>
    )

    const loginOrRegister =  (
        <NavBtn>
            <NavBtnLink style={{ display: currentUser ? "none" : undefined }} to= '/sign-up'>Sign Up</NavBtnLink>
            <NavBtnLink style={{ display: currentUser ? "none" : undefined }} to='/sign-in'>Sign In</NavBtnLink>
        </NavBtn>
    )

    const logout = (
        <button style={ {display: currentUser ? undefined : "none"}} className={style.logoutLink} onClick={() => {
            setUser(undefined)
            document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        }}>LOGOUT</button>
    )

    return (
        <Nav>
            <Bars />
            <NavMenu>
                <NavLink to='/' activestyle="true">
                    Home
                </NavLink>
                <NavLink to='/info' activestyle="true">
                    About
                </NavLink>
                <NavLink to='/leadership/1' activestyle="true">
                    Rankings
                </NavLink>
                {profile}
                {play}
            </NavMenu>
            {loginOrRegister}
            {logout}
        </Nav>
    );
}

