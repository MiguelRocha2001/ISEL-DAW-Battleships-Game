import * as React from "react";
import style from "./Loading.module.css";

export function Loading() {
    return (
        <div className={style.loaderContainer}>
            <div className={style.spinner}></div>
        </div>
    );
};