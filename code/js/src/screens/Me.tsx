import * as React from 'react'
import {Services, UserHome} from '../services'
import {Link} from 'react-router-dom'
import {ShowSirenProperties} from "../utils/ShowSirenProperties";
import style from "./Me.module.css"
import {UserDetail} from "./Commons";
import {Loading} from "./Loading";
import {useEffect, useState} from "react";

export function Me() {
    const response = Services.fetchUserHome()
    console.log(response)
    if (typeof response === "string") {
        if (response === "Loading") {
            return <Loading />
        }
        //redirects to login page
        return (
            <div>
                <p><h2>Please Login before accessing your profile</h2></p>
                <p><Link id={style.login} to="/sign-in">Login</Link></p>
            </div>
        )

    } else {
        return (
            <div>
                <UserDetail user={response}/>
                <Link to="/game" className={style.link}>Game Screen</Link>
            </div>
        )
    }
}

function Content({info}: { info: UserHome }) {
    return (
        <table id={style.userInfoDiv}>
            <tr>
                <td className={style.key}>
                    USER_ID
                </td>
                <span className={style.spacer} />
                <td className={style.value}>
                    {info.userId}
                </td>
            </tr>
            <tr>
                <td className={style.key}>
                    USERNAME
                </td>
                <span className={style.spacer} />
                <td className={style.value}>
                    {info.username}
                </td>
            </tr>
        </table>
    )
}