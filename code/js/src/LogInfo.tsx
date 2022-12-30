import * as React from "react";
import style from "./LogInfo.module.css";
import {useSetUser} from "./screens/auth/Authn";

export function LogInfo({ authenticated, onShow, stopOnShow }: { authenticated: boolean, onShow: () => void, stopOnShow: () => void }) {
    const setUser = useSetUser()
    if (authenticated) {
        onShow()
        return (
            <div id={style.logged} className={style.text}>
                <button className={style.logoutLink} onClick={() => {
                    setUser(undefined)
                    stopOnShow()
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