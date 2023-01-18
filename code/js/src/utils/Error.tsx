import * as React from "react";

export function Error({param}:{param : string}) {
    return (
        <p>
            <h6>
                {param}
            </h6>
        </p>
    )
}