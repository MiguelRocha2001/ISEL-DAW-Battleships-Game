import * as React from 'react'
import {
    useState,
} from 'react'

const defaultUrl = "http://localhost:8080/api/users/all/statistics"

export function Rankings() {
    const [editUrl, setEditUrl] = useState(defaultUrl)
    const [url, setUrl] = useState(defaultUrl)
    return (
        <div>
            
        </div>
    )
}