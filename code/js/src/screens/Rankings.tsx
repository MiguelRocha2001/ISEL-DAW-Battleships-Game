import * as React from 'react'
import {
    useState,
} from 'react'
import { Show } from '../utils/Show'

const defaultUrl = "http://localhost:8080/users/all/statistics"

export function Rankings() {
    const [editUrl, setEditUrl] = useState(defaultUrl)
    const [url, setUrl] = useState(defaultUrl)
    return (
        <div>
            <Show url={url} />
        </div >
    )
}