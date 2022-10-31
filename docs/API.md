# daw-battleship-game

## API

### Users

#### POST /users/
Requests the server to create a user with the given username and password, contained in the body.
The server responds with a 201 Created status code and the user's id in the body.
Otherwise, the server responds with a 400 Insecure Password or 400 User Already Exists, error codes.

#### POST /users/tokens
Requests the server to create a token for the given user, contained in the body.
The server responds with a 201 Created status code and the token in the body.
Otherwise, the server could respond with a 400 Invalid Credentials error code.

#### GET /users/:id


#### GET /me
Requests the server to return the user's home info.

#### DELETE /users/:id
Requests the server to delete the user with the given id.
The server responds with a 204 No Content status code.
Otherwise, the server could respond with a 400 User Not Found error code.


### Games

#### POST /games
Requests the server to create a game with user, represented by the token.
The game could not be created immediately, so the server responds with a 202 Accepted status code. The user will be placed in a waiting queue. 
If the game was created successfully, the server responds with a 201 created.
Errors:
- 400 Invalid Token
- 405 Method Not Allowed (if the user is already in a game or in the waiting queue)

#### GET /games/current
Requests the server to return the current game of the user, represented by the token.
The server responds with a 200 OK status code and the gameId in the body.
Otherwise, the server could respond with a 400 Invalid Token or 404 Game Not Found error codes.

#### POST /games/:id/place-ship
Requests the server to place a ship in the game with the given id.
The server responds with a 201 Created status code.
Errors(*): see under...

#### POST /games/:id/move-ship
Requests the server to move a ship in the game with the given id.
The server responds with a 201 Created status code.
Errors(*): see under...

#### POST /games/:id/rotate-ship
Requests the server to rotate a ship in the game with the given id.
The server responds with a 201 Created status code.
Errors(*): see under...

#### POST /games/:id/place-shot
Requests the server to place a shot in the game with the given id.
The server responds with a 201 Created status code.
Errors(*): see under...

Errors:
- 400 Invalid Token
- 404 Game Not Found
- 405 Method Not Allowed (in case the user is not allowed to do the action, maybe because is in a different game state)
- 405 Method Not Allowed (in case the user is not allowed to do the action, because this one is invalid)

#### POST /games/:id/confirm-fleet
Requests the server to confirm the fleet of the user in the game with the given id.
The server responds with a 201 Created status code.
Erros:
- 400 Invalid Token
- 404 Game Not Found
- 405 Method Not Allowed (in case the user is not allowed to do the action, maybe because is in a different game state)


