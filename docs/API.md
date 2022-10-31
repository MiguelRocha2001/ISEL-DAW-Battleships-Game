# daw-battleship-game

## API

<p>

The API, that supports the Battleship game server, can be divided in three mains groups:
- Home
- Users
- Games

These are described in the following sections.
</p><br>

### HOME

#### GET /
<p>

Requests the server to send the home server representation.
- Doesnt require any parameters nor authorization.
The received representation contains actions to create/login a user.

</p><br>

#### GET /server-info
<p>

Requests the server to send the server information.
- Doesnt require any parameters nor authorization.
</p><br>

### Users

#### POST /users/
<p>

Requests the server to create a user with the given username and password, contained in the body.
The server responds with a 201 Created status code and the user's id in the body.
Otherwise, the server responds with a 400 Insecure Password or 400 User Already Exists, error codes.

The received representation contains a link to request a newly created token.
</p><br>

#### POST /users/tokens
<p>

Requests the server to create a token for the given user, contained in the body.
The server responds with a 201 Created status code and the token in the body.
Otherwise, the server could respond with a 400 Invalid Credentials error code.

The received representation contains an action to request a new game.
</p><br>



#### GET /users/:id


#### GET /me
<p>

Requests the server to return the user's home info.
</p><br>

#### DELETE /users/:id
<p>

Requests the server to delete the user with the given id.
The server responds with a 204 No Content status code.
Otherwise, the server could respond with a 400 User Not Found error code.
</p><br>

#### GET /user/statistics
<p>

Requests the server to return the user's statistics.
The server responds with a 200 OK status code and the statistics in the body. This include a list of the user's games, with the following information:
- username
- wins
- number of games
</p><br>



### Games

#### POST /games
<p>

Requests the server to create a game with user, represented by the token.
The game could not be created immediately, so the server responds with a 202 Accepted status code. The user will be placed in a waiting queue. 
If the game was created successfully, the server responds with a 201 created.
Errors:
- 400 Invalid Token
- 405 Method Not Allowed (if the user is already in a game or in the waiting queue)

If the game was created, the received representation contains a link to request the game representation. Otherwise, returns a link to ask for the current gameId.
</p><br>

#### GET /games/current
<p>

Requests the server to return the current game of the user, represented by the token.
The server responds with a 200 OK status code and the gameId in the body.
Otherwise, the server could respond with a 400 Invalid Token or 404 Game Not Found error codes.
</p><br>

#### POST /games/:id/place-ship
<p>

Requests the server to place a ship in the game with the given id.
The server responds with a 201 Created status code.
Errors(*): see under...
Note(*): see under...
</p><br>

#### POST /games/:id/move-ship
<p>

Requests the server to move a ship in the game with the given id.
The server responds with a 201 Created status code.
Errors(*): see under...
Note(*): see under...
</p><br>

#### POST /games/:id/rotate-ship
<p>

Requests the server to rotate a ship in the game with the given id.
The server responds with a 201 Created status code.
Errors(*): see under...
Note(*): see under...
</p><br>

#### POST /games/:id/confirm-fleet
<p>

Requests the server to confirm the fleet of the user in the game with the given id.
The server responds with a 201 Created status code.
Errors:
- 400 Invalid Token
- 404 Game Not Found
- 405 Method Not Allowed (in case the user is not allowed to do the action, maybe because is in a different game state)
Note(*): see under...
</p><br>

#### POST /games/:id/place-shot
<p>

Requests the server to place a shot in the game with the given id.
The server responds with a 201 Created status code.
Errors(*): see under...
Note(*): see under...

Errors:
- 400 Invalid Token
- 404 Game Not Found
- 405 Method Not Allowed (in case the user is not allowed to do the action, maybe because is in a different game state)
- 405 Method Not Allowed (in case the user is not allowed to do the action, because this one is invalid)
</p><br>

#### GET /games/{id}/my-fleet
<p>

Requests the server the current fleet of the user in the game with the given id.
The server responds with a 200 OK status code and the fleet in the body. This fleet is basically its board representation.
Errors:
- 400 Invalid Token
- 404 Game Not Found

Note(*): The received representation contains a link to request the game representation.
</p><br>


#### GET /games/{id}/opponent-fleet
<p>

Requests the server the current opponent fleet of the user in the game with the given id.
The server responds with a 200 OK status code and the fleet in the body. This fleet is basically the opponent board representation.
Errors:
- 400 Invalid Token
- 404 Game Not Found
</p><br>

#### GET /games/{id}/state
<p>

Requests the server the current state of the game with the given id.
The server responds with a 200 OK status code and the state in the body. This state is basically the game state/phase. Could be one of the following:
- NOT_STARTED
- FLEET_SETUP
- WAITING
- BATTLE
- FINISHED
Errors:
- 400 Invalid Token
- 404 Game Not Found
</p><br>

#### GET /games/{id}
<p>

Requests the server the current game with the given id.
The server responds with a 200 OK status code and the game in the body. The game representation includes the gameId, both board representations, the state, the players and configuration.
</p><br>

#### DELETE /games/{id}
<p>

Requests the server to delete the game with the given id.
The server responds with a 204 No Content status code.
- Doesnt require any parameters nor authorization.
Errors:
- 404 Game Not Found
</p><br>