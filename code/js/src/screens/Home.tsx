import * as React from 'react'
import {
    useState,
} from 'react'
import { Show } from '../utils/Show'
import {
    createBrowserRouter, Link, RouterProvider, useParams,
} from 'react-router-dom'

const defaultUrl = "http://localhost:8080/api"

export function Home() {
    const [editUrl, setEditUrl] = useState(defaultUrl)
    const [url, setUrl] = useState(defaultUrl)
    return (
        <div>
            <h1>Home</h1>
            <Show url={url} />
        </div>
    )
}