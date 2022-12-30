import {auth} from "./server_info/auth";
import * as React from "react";
import style from "./LogInfo.module.css";
import { Navigate } from 'react-router-dom';
import {useSetUser} from "./screens/auth/Authn";
import {useState} from "react";

export function LogInfo({ onShow, stopOnShow }) {
    const authenticated = auth.useAuthentication(undefined)
    const setUser = useSetUser()
    /*
    const [redirect, setRedirect] = useState<string>(undefined)
    if(redirect) {
        return <Navigate to={redirect} state={{source: location.pathname}} replace={true}/>
    }
     */
    if (authenticated) {
        onShow()
        return (
            <div id={style.logged} className={style.text}>
                <button className={style.logoutLink} onClick={() => {
                    auth.setToken(undefined)
                    setUser(undefined)
                    stopOnShow()
                    // setRedirect("/")
                }}>LOGOUT</button>
            </div>
        )
    } else {
        return (
            <span id={style.notLogged} className={style.text}>
               <strong>Want to play?
                Login or register!</strong>
            </span>
        )
    }
}