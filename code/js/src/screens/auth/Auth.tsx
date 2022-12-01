import * as React from 'react'
import { useState } from 'react'
import { ChangeEvent } from 'react'
import { MouseEvent } from 'react'
import { AuthnContainer } from './Authn'
import { navigation } from '../../navigation'

export function Auth() {
    return <AuthnContainer children={
        <InputForm />
    } />
}


// TODO -> form is given by hypermedia (Action fields)
function InputForm() {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')

    function onUsernameChangeHandler(event : ChangeEvent<HTMLInputElement> ){
        console.log("ChangeHandler")
        if(event.target.value.length < 12){
            setUsername(event.target.value)
            setError('')
        }
        else
            setError("Length should be < 5")
    }

    function onPasswordChangeHandler(event : ChangeEvent<HTMLInputElement> ){
        console.log("ChangeHandler")
        if(event.target.value.length < 12){
            setPassword(event.target.value)
            setError('')
        }
        else
            setError("Length should be < 5")
    }

    function onClickHandler(event : MouseEvent<HTMLButtonElement> ){
        navigation.fetchToken([
            {name: "username", value: username},
            {name: "password", value: password},
        ])
    }

    return (
        <div>
            <input onChange={onUsernameChangeHandler} value={username} type="text" /> 
            <input onChange={onPasswordChangeHandler} value={password} type="text" /> 
            <button onClick={onClickHandler}> Click </button>
            {error}
        </div>
    )
}