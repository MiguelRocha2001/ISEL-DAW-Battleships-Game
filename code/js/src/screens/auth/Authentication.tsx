import * as React from "react"
import {useState} from "react"
import {Navigate, useLocation, useNavigate} from "react-router-dom"
import {Services} from "../../services"
import {useSetUser} from "./Authn"
import styles from './Auth.module.css'
import {Logger} from "tslog";


const logger = new Logger({ name: "Authentication" });

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

export function Authentication({title, action}: { title: string, action: Action}) {
    const [inputs, setInputs] = useState({
        username: "",
        password: "",
    })
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [error, setError] = useState(undefined)
    const [checkUsername, setCheckUsername] = useState("")
    const [successSignUp, setSuccessSignUp] = useState("")
    const [redirect, setRedirect] = useState<string>(undefined)
    const setUser = useSetUser()
    const navigate = useNavigate()
    const location = useLocation()
    const styleId = (title === "Sign in")? styles.signIn : styles.signUp
    if(redirect) {
        return <Navigate to={redirect} replace={true}/>
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
            console.log("Logging in")
            authenticate(username, password)
                .then(token => {
                    setIsSubmitting(false)
                    if (token) {
                        setUser(token)
                        setRedirect(location.state?.source?.pathname || "/me")
                    } else {
                        setError("Some error has occurred, please go to the home page and try again")
                    }
                })
                .catch(error => {
                    logger.error('Login: ', error)
                    setIsSubmitting(false)
                    setError("Invalid username or password")
                })
        } else {
            console.log("Registering")
            createUser(username, password)
                .then((userId) => {
                    setIsSubmitting(false)
                    if(userId) {
                        setSuccessSignUp("User created successfully, you can now sign in")
                        setError("")
                        navigate("/sign-in")
                    }
                    else setError("Some error has occurred, please go to the home page and try again")
                    // setRedirect(location.state?.source?.pathname || "/sign-in") // fixme - results in endless loop
                })
                .catch(error => {
                    logger.error('Create user: ', error)
                    setIsSubmitting(false)
                    setError("Unfortunately, this username already exists")
                })
        }
    }

    return (
        <div>
            <div id={styleId} className={styles.signPage}>
                <h2 id={styles.title}>{title}</h2>
                <form onSubmit={handleSubmit} className={styles.form}>
                    <fieldset className={styles.fieldset} disabled={isSubmitting}>
                        <div id={styles.fieldDiv}>
                            <div>
                                <input id="username" className={styles.input} type="text" name="username" value={inputs.username} required={true} placeholder={"Username"} onChange={handleChange} />
                                <p>{checkUsername}</p>
                            </div>
                            <div>
                                <input
                                    id="password" className={styles.input} pattern="(?=.*[a-z])(?=.*[A-Z]).{5,}" title="Must contain at least one uppercase and lowercase letter, and at least 5 or more characters" type="password" required={true} name="password" placeholder={"Password"} value={inputs.password} onChange={handleChange} />
                            </div>
                        </div>
                        <div id={styles.signButton}>
                            <button className={styles.confirmButton} type="submit">{title}</button>
                        </div>
                    </fieldset>
                </form>
            </div>
            <div>
                <p><h4>{error}</h4></p>
                <p><h4>{successSignUp}</h4></p>
            </div>

        </div>
    )
}