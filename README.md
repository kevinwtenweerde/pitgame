# The Pit Game

The Pit game is a game powered by Spring Boot and Thymeleaf.

# Notes
- The game is played by two players
- Each player has six pits in front of them
- Each pit contains six stones
- Each player has a big pit that is empty

# Board layout
  Direction of play ---->
+---+---+---+----+----+----+
| 1 | 2 | 3 | 4  | 5  | 6  |
+---+---+---+----+----+----+
| 7 | 8 | 9 | 10 | 11 | 12 |
+---+---+---+----+----+----+
    <---- Direction of play
# Rules
- The starting player picks up all stones in a pit
- The pits are distributed over the pits on the right hand side
- Stones are also put in the own big pits
- When the last stone that has to be distributed lands in the big pit, the player gets another go
- Stones can be captured when the last stone that needs to be distributed ends up in an empty pit. The stones from the opposing pit and the last pit hit are being put in the big pit.

# Winning
- The game ends when a player has no more stones in his small pits
- The players collect all stones from their small pits and put them in the their big pit
- The player that has the most stones in their big pit wins

# Tech
- Spring Boot 2.1.2
- Project Lombok 1.18.4
- Bootstrap 4.2.1
- PowerMock 2.0.0

# Notes
- The state of the board is persisted in an in memory database (h2)
> By persisting the board in an database the board is available anywhere needed without passing it around the application
- To reduce complexity for this version there will always be one game
- Pits are hardcoded in to a board layout where they are assigned to an id to target from the frontend
- The application properties are being used to configure the application
> During application initialization the properties will be validated so the game will only start in a valid state
- The game state is a layer over the board that reads the board and determines the current game state
- A restart function in the application will allow users to start over
- > This really just restarts the spring application
