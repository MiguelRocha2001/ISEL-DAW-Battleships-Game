import * as React from 'react'
import {Bars, Nav, NavBtn, NavBtnLink, NavLink, NavMenu,} from './NavbarElements';
import {useCurrentUser, useSetUser} from "./screens/auth/Authn";
import style from "./LogoutButton.module.css";

export function Navbar () {
    const currentUser = useCurrentUser()
    const setUser = useSetUser()
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
                <NavLink to='/' activeStyle>
                    Home
                </NavLink>
                <NavLink to='/info' activeStyle>
                    About
                </NavLink>
                <NavLink to='/leadership' activeStyle>
                    Rankings
                </NavLink>
                <NavLink to='/me' activeStyle>
                    Profile
                </NavLink>
                <NavLink to='/game' activeStyle>
                    Play
                </NavLink>
            </NavMenu>
            {loginOrRegister}
            {logout}
        </Nav>
    );
}

