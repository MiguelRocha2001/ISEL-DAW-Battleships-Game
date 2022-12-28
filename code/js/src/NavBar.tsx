import * as React from 'react'
import {
    Nav,
    NavLink,
    Bars,
    NavMenu,
    NavBtn,
    NavBtnLink,
} from './NavbarElements';
import {auth} from "./server_info/auth";
import style from "../static-files/css/battleships/commons.css";

export function Navbar ({isActive}) {
    const content =(isActive)?(
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
            {content}
        </Nav>
    );
};

