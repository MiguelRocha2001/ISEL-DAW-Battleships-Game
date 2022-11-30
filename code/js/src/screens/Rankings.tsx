import * as React from 'react'
import { Show } from '../utils/Show'
import { navigation } from '../navigation'

export function Rankings() {
    const content = navigation.fetchBattleshipRanks()
    return (
        <div>
            <Show content={content} property="users" />
        </div>
    )
}