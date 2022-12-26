import * as React from 'react'
import {Services, User, UserHome} from '../services'
import {Link} from 'react-router-dom'
import style from "./Me.module.css"
import {UserDetail} from "./Commons";
import {Loading} from "./Loading";
import {useEffect, useState} from "react";

export function Me() {
    const response = Services.fetchUserHome()

    if (typeof response === "string") {
        if (response === "Loading") {
            return <Loading />
        }
        else {
            return <p>{response}</p>
        }
    } else {
        return (
            <div>
                <UserDetail user={response}/>
                <Link to="/game" className={style.link}>Game Screen</Link>
            </div>
        )
    }
}