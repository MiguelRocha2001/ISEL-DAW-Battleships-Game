import * as React from 'react'
import {
    useState,
} from 'react'
import { Show } from '../utils/Show'
import { useFetch } from '../utils/useFetch'
import { navigation } from '../navigation'

export function Info() {
    const content = navigation.fetchServerInfo()
    return (
        <div>
            <Show content={content} property="authors" />
        </div >
    )
}