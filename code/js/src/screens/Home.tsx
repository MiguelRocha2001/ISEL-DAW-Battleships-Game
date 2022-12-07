import * as React from 'react'
import {
    useState,
    useEffect,
} from 'react'
import { Show } from '../utils/Show'
import { Link } from 'react-router-dom'
import { navigation } from '../navigation'

export function Home() {
    const content = navigation.useFetchHome()
    return (
        <div>
            <Show content={content} property="title" />
            <ul>
                <li><Link to="/info">Info</Link></li>
                <li><Link to="/rankings">Ranks</Link></li>
                <li><Link to="/auth">Auth</Link></li>
            </ul>
        </div>
    )
}