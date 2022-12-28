import {auth} from "./server_info/auth";
import * as React from "react";
import style from "./LogInfo.module.css";
import { Navigate } from 'react-router-dom';

export function LogInfo({onShow,stopOnShow}) {
    const authenticated = auth.useAuthentication(undefined)
    if (authenticated) {
        onShow()
        return (
            <div id={style.logged} className={style.text}>
                <button className={style.logoutLink} onClick={() => {
                    auth.setToken(undefined)
                    stopOnShow()
                    return <Navigate to="/" />;
                }
                }>LOGOUT</button>
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