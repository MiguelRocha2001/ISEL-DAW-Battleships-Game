# Battleship Game API 

## Description 

The **Battleship Game API** provides access to a classic battleship game where players can challenge each other by placing their ships on a board and taking turns trying to sink each other's ships. The API allows players to join a game, place their ships, and take turns firing at their opponent's ships.

The API uses a *RESTful* design and returns data in *JSON* format. With the Battleship Game API, developers can easily create applications and websites that allow players to play the classic game of battleship. Whether you're building a mobile app or a website, the Battleship Game API provides a simple and straightforward way to add the game to your platform.


## Organization

The API, that supports the Battleship game server, can be divided in three mains groups:
- Home
- Users
- Games

*****

###  __***Home Endpoints***__


Get home representation, *is the starting point of the aplication*

``` GET / ```

*Parameters*
- (none)

</p><br>

#

Get the server information

``` GET /info ```

*Parameters*
- (none)
</p><br>

*****

###  __***User Endpoints***__

#

This endpoint allows the creation of a *new* user in the system.

``` POST /users ```

*Parameters*
  Body:
    - username (String) : Name of the new user
    - password (String) : Password of the new user

*Sucessfull response*

- Status code 201: It is returned the user's id. The received representation contains a link to request a newly created token.

*Possible error responses*

- Status code 400: The password is insecure or the user already exists.

</p><br>

#

Creates a new token for a given user

``` POST /users/token ```

*Parameters*
  Body:
    - username (String) : Name of the new user
    - password (String) : Password of the new user

*Sucessfull response*

- Status code 201: It is returned the user's token. The received representation contains an action to request a new game.

*Possible error responses*

- Status code 400: Credentials are not correct.


</p><br>

#

Get the information about the logged user.

- Authorization Token: The API requires a valid authorization token to be sent with each request in a cookie.

``` GET /me ```

*Parameters*
- (none)

*Possible error responses*

- Status code 401:  The authorization token provided in the cookie is invalid.

#

Get the information about one user.

``` GET /users/{id} ```

*Parameters*
  Uri:
  - id (Integer) : Identifier of a user.

*Possible error responses*

- Status code 400:  The given user is not valid.

#

Get the all the users statistics.

``` GET /users/all/statistics ```

*Sucessfull response*

- Status code 200: The statistics of the users in the body.This include a list of the  games, with the following information: username, wins, number of games


*****

###  __***Game Endpoints***__

*Note* : During this section is metioned the word "state":
Is basically the game state/phase. Could be one of the following:
- NOT_STARTED
- FLEET_SETUP
- WAITING
- BATTLE
- FINISHED

#

Requests the server to create a game for a user. If the game was created, the received representation contains a link to request the game representation. Otherwise, returns a link to ask for the current gameId.

- Authorization Token: The API requires a valid authorization token to be sent with each request in a cookie.

``` POST /my/games ```

*Parameters*
  Body:
    - boardSize (Number) : Size of the board
    - nShotsPerRound (Number) : Number of shots per round
    - roundTimeout (Number) : Timout time

*Sucessfull response*

- Status code 202: The user will be placed in a waiting queue,it happens when the user is waiting for an opponent,but for two players match, both configurations must be the same.

- Status code 201: The game was successfully created.

*Possible error responses*

- Status code 401: The authorization token provided in the cookie is invalid.

- Status code 405 : The user is already in a game or in the waiting queue

#

Requests the server to return the current game of the user.

- Authorization Token: The API requires a valid authorization token to be sent with each request in a cookie.

``` GET /my/games/current ```

*Sucessfull response*

- Status code 200: The game identifier in the body.


*Possible error responses*

- Status code 404: The game does not exist.

- Status code 401 : The authorization token provided in the cookie is invalid.

#

Requests the server to place a ship in the game with the given identifier of the game.

- Authorization Token: The API requires a valid authorization token to be sent with each request in a cookie.

``` POST /games/:id/place-ship ```

*Possible error responses*

- Status code 401:  The authorization token provided in the cookie is invalid.

- Status code 404 : The game does not exist.

- Status code 405 : in case the user is not allowed to preform the action, maybe because is in a different game state or in case the user is not allowed to do the action, because it is invalid

#

Requests the server to move a ship in the game with the given identifier.

- Authorization Token: The API requires a valid authorization token to be sent with each request in a cookie.

``` POST /games/:id/move-ship ```

*Possible error responses*

- Status code 401:  The authorization token provided in the cookie is invalid.

- Status code 404 : The game does not exist.

- Status code 405 : in case the user is not allowed to preform the action, maybe because is in a different game state or in case the user is not allowed to do the action, because it is invalid

#

Requests the server to rotate a ship in the game with the given id.

- Authorization Token: The API requires a valid authorization token to be sent with each request in a cookie.

``` POST /games/:id/rotate-ship ```

*Possible error responses*

- Status code 401:  The authorization token provided in the cookie is invalid.

- Status code 404 : The game does not exist.

- Status code 405 : in case the user is not allowed to preform the action, maybe because is in a different game state or in case the user is not allowed to do the action, because it is invalid

#

Requests the server to confirm the fleet of the user in the game with the given id.

- Authorization Token: The API requires a valid authorization token to be sent with each request in a cookie.

``` POST /games/:id/confirm-fleet ```

*Possible error responses*

- Status code 401:  The authorization token provided in the cookie is invalid.

- Status code 404 : The game does not exist.

- Status code 405 : in case the user is not allowed to preform the action, maybe because is in a different game state or in case the user is not allowed to do the action, because it is invalid

#

Requests the server to place a shot in the game with the given id.

- Authorization Token: The API requires a valid authorization token to be sent with each request in a cookie.

``` POST /games/:id/place-shot ```

*Possible error responses*

- Status code 401:  The authorization token provided in the cookie is invalid.

- Status code 404 : The game does not exist.

- Status code 405 : in case the user is not allowed to preform the action, maybe because is in a different game state or in case the user is not allowed to do the action, because it is invalid

#

Requests the server the current fleet of the user in the game with the given id.

``` GET /games/{id}/my-fleet ```
<p>


*Sucessfull response*

- Status code 200: The server responds with the fleet in the body of the response. This fleet is basically the board representation.

*Possible error responses*

- Status code 401:  The authorization token provided in the cookie is invalid.

- Status code 404 : The game does not exist.

#

*Same objective as the above but it is the opponent board*

``` GET /games/{id}/opponent-fleet ```

#

Requests the server the current state of the game with the given id.

``` GET /games/{id}/state ```
<p>


*Possible error responses*

- Status code 401:  The authorization token provided in the cookie is invalid.

- Status code 404 : The game does not exist.

#

Requests the server the current game with the given id.

``` GET /games/{id} ```
<p>

*Sucessfull response*

- Status code 200:   The game representation includes the gameId, both boardS representations, the state, the players and configuration.

#

Returns the information if the user is in the queue

- Authorization Token: The API requires a valid authorization token to be sent with each request in a cookie.

``` GET /games/queue/me ```

*Possible error responses*

- Status code 401:  The authorization token provided in the cookie is invalid.

#

Quit a game given the game identifier

- Authorization Token: The API requires a valid authorization token to be sent with each request in a cookie.

``` POST /games/{id} ```

*Possible error responses*

- Status code 401:  The authorization token provided in the cookie is invalid.
- Status code 400:  The game is invalid.

#

Quit waiting for a game to start given the game identifier.The user leaves the queue

- Authorization Token: The API requires a valid authorization token to be sent with each request in a cookie.

``` DELETE /games/queue/me ```

*Possible error responses*

- Status code 401:  The authorization token provided in the cookie is invalid.


