import {User, UserHome} from "../services";
import style from "./Me.module.css";
import * as React from "react";

export function UserDetail({user}: { user: UserHome | User }) {
    return (
        <table id={style.userInfoDiv}>
            <tr>
                <td className={style.key}>
                    USER_ID
                </td>
                <span className={style.spacer} />
                <td className={style.value}>
                    {user.id}
                </td>
            </tr>
            <tr>
                <td className={style.key}>
                    USERNAME
                </td>
                <span className={style.spacer} />
                <td className={style.value}>
                    {user.username}
                </td>
            </tr>
        </table>
    )
}