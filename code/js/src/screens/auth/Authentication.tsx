import * as React from "react"
import {useState} from "react"
import {Navigate, useLocation, useNavigate} from "react-router-dom"
import {Services} from "../../services"
import {useSetUser} from "./Authn"
import styles from './Auth.module.css'

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
    const [checkUsername, setCheckUsername] = useState("")
    const [redirect, setRedirect] = useState(false)
    const setUser = useSetUser()
    const navigate = useNavigate()
    const location = useLocation()
    if(redirect) {
        return <Navigate to={location.state?.source?.pathname || "/me"} replace={true}/>
    }
    function handleChange(ev: React.FormEvent<HTMLInputElement>) {
        const name = ev.currentTarget.name
        if(name === "username") {
            if( ev.currentTarget.value.length > 20)  {
                setCheckUsername("Username must be less less than 20 characters")
                ev.currentTarget.value = ev.currentTarget.value.substring(0, 20)
            } else setCheckUsername("")
        }
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
            <h2 id={styles.title}>{title}</h2>
            <form onSubmit={handleSubmit} className={styles.form}>
                <fieldset className={styles.fieldset} disabled={isSubmitting}>
                    <div>
                        <input id="username" className={styles.input} type="text" name="username" value={inputs.username} required={true} placeholder={"USERNAME"} onChange={handleChange} />
                        <p>{checkUsername}</p>
                    </div>
                    <div>
                        <input
                            id="password" className={styles.input} type="password" minLength={8} required={true} name="password" placeholder={"PASSWORD"} value={inputs.password} onChange={handleChange} />
                    </div>
                    <div id={styles.signButton}>
                        <button className={styles.confirmButton} type="submit">{title}</button>
                    </div>
                </fieldset>
                {error}
            </form>
        </div>
    )
}