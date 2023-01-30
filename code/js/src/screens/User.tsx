import {useParams} from "react-router-dom";
import {Fetching, Services} from "../services";
import * as React from "react";
import {UserDetail} from "./Commons";
import {ErrorScreen} from "../utils/ErrorScreen";
import {Loading} from "./Loading";


export function User() {
    const { id } = useParams()
    const result = Services.getUser(+id)

    if (result instanceof Error) {
        return <ErrorScreen message={result.message}/>
    } else if (result instanceof Fetching) {
        return <Loading />
    } else {
        return <UserDetail user={result} />
    }
}