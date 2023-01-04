import {useParams} from "react-router-dom";
import {Services} from "../services";
import * as React from "react";
import {UserDetail} from "./Commons";
import styles from './User.module.css'


export function User() {
    const { id } = useParams()
    const resp = Services.getUser(+id)

    if (typeof resp == "string") {
        return (
            <p>{resp}</p>
        )
    }
    else {
        return (
            <UserDetail user={resp} />
        )
    }
}