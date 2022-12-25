import * as React from "react"
import {useState} from "react"
import {Navigate, useLocation, useNavigate} from "react-router-dom"
import {Services} from "../../services"
import {useSetUser} from "./Authn"

export async function authenticate(username: string, password: string): Promise<string | undefined> {
    return Services.fetchToken([
        {name: "username", value: username},
        {name: "password", value: password},
    ])
}

export async function createUser(username: string, password: string): Promise<string | undefined> {
    return Services.createUser([
        {name: "username", value: username},
        {name: "password", value: password},
    ])
}

type Action = "login" | "register"

export function Authentication({title, action}: { title: string, action: Action }) {
    const [inputs, setInputs] = useState({
        username: "",
        password: "",
    })
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [error, setError] = useState(undefined)
    const [redirect, setRedirect] = useState(false)
    const setUser = useSetUser()
    const navigate = useNavigate()
    const location = useLocation()
    if(redirect) {
        return <Navigate to={location.state?.source?.pathname || "/me"} replace={true}/>
    }
    function handleChange(ev: React.FormEvent<HTMLInputElement>) {
        const name = ev.currentTarget.name
        setInputs({ ...inputs, [name]: ev.currentTarget.value })
        setError(undefined)
    }

    function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
        ev.preventDefault()
        setIsSubmitting(true)
        const username = inputs.username
        const password = inputs.password
        if (action === "login") {
            authenticate(username, password)
                .then(token => {
                    setIsSubmitting(false)
                    if (token) {
                        const redirect = location.state?.source?.pathname || "/me"
                        setRedirect(true)
                    } else {
                        setError("Invalid username or password")
                    }
                })
                .catch(error => {
                    setIsSubmitting(false)
                    setError(error.message)
                })
        } else {
            createUser(username, password)
                .then(() => {
                    setIsSubmitting(false)
                    // Nothing to do
                })
                .catch(error => {
                    setIsSubmitting(false)
                    setError(error.message)
                })
        }
    }
    
    return (
        <div>
            <h3>{title}</h3>
            <form onSubmit={handleSubmit}>
                <fieldset disabled={isSubmitting}>
                    <div>
                        <label htmlFor="username">Username</label>
                        <input id="username" type="text" name="username" value={inputs.username} onChange={handleChange} />
                    </div>
                    <div>
                        <label htmlFor="password">Password</label>
                        <input id="password" type="text" name="password" value={inputs.password} onChange={handleChange} />
                    </div>
                    <div>
                        <button type="submit">Login</button>
                    </div>
                </fieldset>
                {error}
            </form>
        </div>
    )
}