import * as React from 'react'
import { useState } from 'react'
import { ChangeEvent } from 'react'
import { MouseEvent } from 'react'
import { AuthnContainer } from './Authn'
import { navigation } from '../../navigation'

export function Auth() {
    return <AuthnContainer children={[
        <InputForm key = '1' title='Login' onClickHandlerParam={ async (username, password) => 
            navigation.fetchToken([
                {name: "username", value: username},
                {name: "password", value: password},
            ])
        }/>,
        <InputForm key = '2' title='Register' onClickHandlerParam={ (username, password) => 
            navigation.registerNewUser([
                {name: "username", value: username},
                {name: "password", value: password},
            ])
        }/>
    ]}/>
}

const MAX_FIELD_LENGTH = 50

// TODO -> form is given by hypermedia (Action fields)
function InputForm( {title, onClickHandlerParam}: {title: string, onClickHandlerParam?: (username, password) => void}) {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')

    function onUsernameChangeHandler(event : ChangeEvent<HTMLInputElement> ){
        if(event.target.value.length < MAX_FIELD_LENGTH) {
            setUsername(event.target.value)
            setError('')
        }
        else
            setError("Length should be < " + MAX_FIELD_LENGTH)
    }

    function onPasswordChangeHandler(event : ChangeEvent<HTMLInputElement> ) {
        if(event.target.value.length < MAX_FIELD_LENGTH){
            setPassword(event.target.value)
            setError('')
        }
        else
            setError("Length should be < " + MAX_FIELD_LENGTH)
    }

    function onClickHandler(event : MouseEvent<HTMLButtonElement> ) {
        event.preventDefault()
        if(onClickHandlerParam) onClickHandlerParam(username, password)
    }

    return (
        <div>
            <label>{title}</label>
            <br/>
            <input onChange={onUsernameChangeHandler} value={username} type="text" /> 
            <input onChange={onPasswordChangeHandler} value={password} type="text" /> 
            <button onClick={onClickHandler}> Click </button>
            {error}
        </div>
    )
}