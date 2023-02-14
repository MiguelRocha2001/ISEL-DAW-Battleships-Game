import * as React from 'react'
import {createContext, useContext, useEffect, useState} from 'react'
import {Services} from "../../services";

type ContextType = {
    user: string | undefined,
    setUser: (v: string | undefined) => void
}
const LoggedInContext = createContext<ContextType>({
    user: undefined,
    setUser: () => { },
})

export function AuthnContainer({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState(undefined)

    useEffect( () => {
        async function fetchUser () {
            const user = await extractTokenFromCookie()
            setUser(user)
        }
        fetchUser()
    }, [])

    return (
        <LoggedInContext.Provider value={{ user: user, setUser: setUser }}>
            {children}
        </LoggedInContext.Provider>
    )
}

async function extractTokenFromCookie(): Promise<string | undefined> {
    const isLogged = await Services.isLogged()
    if (isLogged) {
        return "logged"
    } else {
        return undefined
    }
}

export function useCurrentUser() {
    return useContext(LoggedInContext).user
}

export function useSetUser() {
    return useContext(LoggedInContext).setUser
}
