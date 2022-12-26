import {useParams} from "react-router-dom";
import {ServerInfo, Services, User} from "../services";
import * as React from "react";
import {UserDetail} from "./Commons";

export function User() {
    const { id } = useParams()
    console.log(id)
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