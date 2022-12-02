import * as React from 'react'
import {
    useState,
    useEffect,
} from 'react'
import { Show } from '../utils/Show'
import { navigation } from '../navigation'

export function Rankings() {
    const [content, setContent] = useState(undefined)
    React.useEffect(() => {
        async function fetchContent() {
            const content = await navigation.fetchBattleshipRanks()
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