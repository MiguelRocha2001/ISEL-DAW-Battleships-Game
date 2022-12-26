import {auth} from "./server_info/auth";
import * as React from "react";
import style from "./LogInfo.module.css";

export function LogInfo() {
    const authenticated = auth.useAuthentication(undefined)
    if (authenticated) {
        return (
            <div id={style.logged} className={style.text}>
                <text id={style.loggedText}>LOGGED</text>
                <button className={style.logoutLink} onClick={() => auth.setToken(undefined)}>LOGOUT</button>
            </div>
        )
    } else {
        return (
            <span id={style.notLogged} className={style.text}>
                NOT LOGGED !
            </span>
        )
    }
}