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
                {/* Second Nav */}
                {/* <NavBtnLink to='/sign-in'>Sign In</NavBtnLink> */}
            </NavMenu>
            <NavBtn>
                <NavBtnLink to= '/sign-up'>Sign Up</NavBtnLink>
                <NavBtnLink to='/sign-in'>Sign In</NavBtnLink>
            </NavBtn>
        </Nav>
    );
};

export default Navbar;