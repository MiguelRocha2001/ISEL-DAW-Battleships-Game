import * as React from "react";

export function ErrorScreen({message} : {message : string}) {
    return (
        <h4>
            Something went wrong: {message}
        </h4>
    )
}