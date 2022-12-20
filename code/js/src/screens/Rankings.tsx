import * as React from 'react'
import {
    useState,
    useEffect,
} from 'react'
import { ShowSirenProperties } from '../utils/ShowSirenProperties'
import { Services } from '../services'

export function Rankings() {
    const content = Services.fetchBattleshipRanks()
    return (
        <div>
            <ShowSirenProperties content={content} properties={['users']} />
        </div >
    )
}