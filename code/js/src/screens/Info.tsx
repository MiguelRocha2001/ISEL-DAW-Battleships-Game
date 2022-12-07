import * as React from 'react'
import {
    useState,
    useEffect,
} from 'react'
import { Show } from '../utils/Show'
import { navigation } from '../navigation'

export function Info() {
    const content = navigation.useFetchServerInfo()
    return (
        <div>
            <Show content={content} property="authors" />
        </div >
    )
}