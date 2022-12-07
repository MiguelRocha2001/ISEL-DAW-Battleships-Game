import * as React from 'react'
import {
    useState,
    useEffect,
} from 'react'
import { Show } from '../utils/Show'
import { Services } from '../services'

export function Rankings() {
    const [content, setContent] = useState(undefined)
    React.useEffect(() => {
        async function fetchContent() {
            const content = await Services.fetchBattleshipRanks()
            setContent(content)
        }
        setContent(undefined)
        fetchContent()
    }, [])
    return (
        <div>
            <Show content={content} property="users" />
        </div>
    )
}