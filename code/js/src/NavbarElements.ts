import {FaBars} from 'react-icons/fa';
import {NavLink as Link} from 'react-router-dom';
import styled from 'styled-components';

export const Nav = styled.nav `
  font-size: 1.2rem;
  width: 100%;
  background: #000;
  height: 85px;
  border-radius: 15px;
  display: flex;
  justify-content: space-between;
  z-index: 12;
  /* Third Nav */
  /* justify-content: flex-start; */
`;
export const NavLink = styled(Link) `
  color: #ffffff;
  font-weight: bold;
  display: flex;
  align-items: center;
  text-decoration: none;
  padding: 0 1rem;
  height: 100%;
  cursor: pointer;
  &.active {
    color: #26FFDF;
  }
  &:hover {
    color: #26FFDF;
  }
`;
export const Bars = styled(FaBars) `
  display: none;
  color: #ffcc00;
  @media screen and (max-width: 768px) {
    display: block;
    position: absolute;
    top: 0;
    right: 0;
    transform: translate(-100%, 75%);
    font-size: 1.8rem;
    cursor: pointer;
  }
`;
export const NavMenu = styled.div `
  display: flex;
  align-items: center;
  margin-right: -24px;
  /* Second Nav */
  /* margin-right: 24px; */
  /* Third Nav */
  /* width: 100vw;
  white-space: nowrap; */
  @media screen and (max-width: 768px) {
    display: none;
  }
`;
export const NavBtn = styled.nav `
  display: flex;
  align-items: center;
  margin-right: 24px;
  /* Third Nav */
  /* justify-content: flex-end;
  width: 100vw; */
  @media screen and (max-width: 768px) {
    display: none;
  }
`;
export const NavBtnLink = styled(Link) `
  border-radius: 0.2em;
  background: white;
  padding: 10px 22px;
  color: #000000;
  font-weight: bold;
  outline: none;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  text-decoration: none;
  /* Second Nav */
  margin-left: 24px;
  &:hover {
    transition: all 0.2s ease-in-out;
    color: #000000;
    background-color: #26FFDF;
  }
`;
