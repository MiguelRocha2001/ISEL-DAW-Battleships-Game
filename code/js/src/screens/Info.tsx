import * as React from 'react'
import {
    useState,
} from 'react'
import { Show } from '../utils/Show'
import { useFetch } from '../utils/useFetch'

const defaultUrl = "http://localhost:8080/api/info"

export function Info() {
    const [url, setUrl] = useState(defaultUrl)
    const content = useFetch(defaultUrl)
    return (
        <div>
            <Show content={content} property="title" />
        </div >
    )
}