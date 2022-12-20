import * as React from 'react'
import {
    useState,
    useEffect,
} from 'react'
import { ShowSirenProperties } from '../utils/ShowSirenProperties'
import { Link } from 'react-router-dom'
import { Services } from '../services'

export function Home() {
    const properties = Services.useFetchHome()
    return (
        <div>
            <ShowSirenProperties content={properties} properties={['title']} />
            <ul>
                <li><Link to="/info">Info</Link></li>
                <li><Link to="/rankings">Ranks</Link></li>
                <li><Link to="/auth">Auth</Link></li>
            </ul>
        </div>
    )
}