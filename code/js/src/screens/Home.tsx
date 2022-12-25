import * as React from 'react'
import {ShowSirenProperties} from '../utils/ShowSirenProperties'
import {Link} from 'react-router-dom'
import {Services} from '../services'

export function Home() {
    const properties = Services.useFetchHome()
    return (
        <ShowSirenProperties content={properties} properties={['title']} />
    )
}