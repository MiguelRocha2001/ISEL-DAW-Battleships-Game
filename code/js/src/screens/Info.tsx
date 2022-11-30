import * as React from 'react'
import {
    useState,
} from 'react'
import { Show } from '../utils/Show'

const defaultUrl = "http://localhost:8080/api/info"

export function Info() {
    const [editUrl, setEditUrl] = useState(defaultUrl)
    const [url, setUrl] = useState(defaultUrl)
    return (
        <div>
            
        </div >
    )
}