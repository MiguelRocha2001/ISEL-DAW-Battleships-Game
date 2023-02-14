import * as React from 'react'
import {createContext, useContext, useState} from 'react'

type ContextType = {
    user: string | undefined,
    setUser: (v: string | undefined) => void
}
const LoggedInContext = createContext<ContextType>({
    user: undefined,
    setUser: () => { },
})

export function AuthnContainer({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState(extractTokenFromCookie())

    return (
        <LoggedInContext.Provider value={{ user: user, setUser: setUser }}>
            {children}
        </LoggedInContext.Provider>
    )
}

function extractTokenFromCookie(): string | undefined {
    const cookieStr = document.cookie
    console.log('cookieStr: ', cookieStr)
    const cookieArr = cookieStr.split(';')
    const cookieArrTrimmed = cookieArr.map((c) => c.trim())
    const token = cookieArrTrimmed.find((c) => c.startsWith('authenticated='))
    if (token) {
        return  token.split('=')[1]
    }
    return undefined
}

export function useCurrentUser() {
    return useContext(LoggedInContext).user
}

export function useSetUser() {
    return useContext(LoggedInContext).setUser
}
