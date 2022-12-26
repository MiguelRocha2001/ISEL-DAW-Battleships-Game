import * as React from 'react'
import {
    Nav,
    NavLink,
    Bars,
    NavMenu,
    NavBtn,
    NavBtnLink,
} from './NavbarElements';

const Navbar = () => {
    return (
        <Nav>
            <Bars />
            <NavMenu>
                <NavLink to='/' activeStyle>
                    Home
                </NavLink>
                <NavLink to='/info' activeStyle>
                    Info
                </NavLink>
                <NavLink to='/leadership' activeStyle>
                    Rankings
                </NavLink>
                <NavLink to='/sign-up' activeStyle>
                    Sign Up
                </NavLink>
                <NavLink to='/me' activeStyle>
                    Me
                </NavLink>
                <NavLink to='/game' activeStyle>
                    Game
                </NavLink>
                {/* Second Nav */}
                {/* <NavBtnLink to='/sign-in'>Sign In</NavBtnLink> */}
            </NavMenu>
            <NavBtn>
                <NavBtnLink to='/sign-in'>Sign In</NavBtnLink>
            </NavBtn>
        </Nav>
    );
};

export default Navbar;