import * as React from "react";
import {NetworkError, ServerError} from "./domain";
import {InvalidArgumentError, ResolutionLinkError} from "../services";

export function ErrorScreen({error} : {error : Error}) {
    const title = "Something went wrong!!!"
    let subTitle: string
    let details: string

    if (error instanceof NetworkError) {
        subTitle = "Network error"
        details = error.message
    } else if (error instanceof ResolutionLinkError) {
        subTitle = "Resolution link error"
        details = error.message
    } else if (error instanceof ServerError) {
        subTitle = "Server error"
        details = error.message
    } else if (error instanceof InvalidArgumentError) {
        subTitle = "Invalid argument"
        details = error.message
    } else {
        subTitle = "Unknown error"
        details = error.message
    }

    return (
        <div>
            <h3>{title}</h3>
            <h4>{subTitle}</h4>
            <p>{details}</p>
        </div>
    )
}