import * as React from 'react'
import {
    useState,
    useEffect,
} from 'react'
import { Show } from '../utils/Show'
import { Services } from '../services'

export function Info() {
    const content = Services.useFetchServerInfo()
    return (
        <div>
            <Show content={content} property="authors" />
        </div >
    )
}