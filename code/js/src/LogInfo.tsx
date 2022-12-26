import {auth} from "./server_info/auth";
import * as React from "react";
import style from "./LogInfo.module.css";

export function LogInfo() {
    const authenticated = auth.useAuthentication(undefined)
    if (authenticated) {
        return (
            <span id={style.logged} className={style.text}>
                LOGGED
            </span>
        )
    } else {
        return (
            <span id={style.notLogged} className={style.text}>
                NOT LOGGED !
            </span>
        )
    }
}