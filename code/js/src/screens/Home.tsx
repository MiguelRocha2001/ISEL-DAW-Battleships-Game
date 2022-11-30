import * as React from 'react'
import { Show } from '../utils/Show'
import { useFetch } from '../utils/useFetch'
import { Link } from 'react-router-dom'
import {
    useState,
} from 'react'

const defaultUrl = "http://localhost:8080/api"

export function Home() {
    const content = useFetch(defaultUrl)
    return (
        <div>
            <Show content={content} property="title" />
            <ol>
                <li><Link to="/info">Info</Link></li>
                <li><Link to="/rankings">Ranks</Link></li>
            </ol>
        </div>
    )
}