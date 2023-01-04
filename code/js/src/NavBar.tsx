import * as React from 'react'
import {Bars, Nav, NavBtn, NavBtnLink, NavLink, NavMenu,} from './NavbarElements';
import {useCurrentUser, useSetUser} from "./screens/auth/Authn";
import style from "./LogoutButton.module.css";

export function Navbar () {
    const currentUser = useCurrentUser()
    const setUser = useSetUser()

    const profile = (currentUser) ? (
        <NavLink to='/me' activestyle="true">
            Profile
        </NavLink>
    ):(
        <NavLink style={{ display: "none" }} to='/me' activestyle="true">
            Profile
        </NavLink>
    )

    const play = (currentUser) ? (
        <NavLink to='/game' activestyle="true">
            Play
        </NavLink>
    ):(
        <NavLink to='/game' style={{ display: "none" }} activestyle="true">
            Play
        </NavLink>
    )

    const loginOrRegister = (currentUser) ? (
        <NavBtn>
            <NavBtnLink style={{ display: "none" }} to= '/sign-up'>Sign Up</NavBtnLink>
            <NavBtnLink style={{ display: "none" }} to='/sign-in'>Sign In</NavBtnLink>
        </NavBtn>
    ):(
        <NavBtn>
            <NavBtnLink to= '/sign-up'>Sign Up</NavBtnLink>
            <NavBtnLink to='/sign-in'>Sign In</NavBtnLink>
        </NavBtn>
    )

    const logout = (currentUser) ? (
        <button className={style.logoutLink} onClick={() => {
            setUser(undefined)
        }}>LOGOUT</button>
    ):(
        <button style={{ display: "none" }} className={style.logoutLink} onClick={() => {
            setUser(undefined)
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
                <NavLink to='/leadership' activestyle="true">
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

