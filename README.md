# HexWars
My first original game project.

RULES OF THE GAME: 
-The game is turn-based. Red player starts the game and has 5 stacks while blue player comes second and has 7 stacks at the start of the game.
-Your Turn: During your turn you have 2 options; you can either click on a hexagon that has no opponent's stacks on or click on the SKIP hexagon and you skip your turn. If you have at least 1 stack left available when you select a hexagon without opponent's stacks, one of your stacks will be placed on the hexagon. The maximum amount of stacks that a hexagon can hold is represented by the number of rectangles in it.
-End Of Turn: At the end of each turn tiles (hexagons) that are full (they hold the maximum number of stacks) will spread pressure on their adjacent tiles. The amount of pressure a full tile puts on adjacent tiles is equal to the number of stacks the full tile holds. For each tile on the map the pressure from all it's adjacent tiles is calculated. If the pressure from one player's stacks is bigger than the pressure from the stacks of his opponent and that tile holds no opponent's stacks, one stack of his color will appear on that tile (this will not decrease the number of stacks he has available). If the tile already holds at least 1 of opponent's stacks then 1 of his stacks will be removed from the tile and be added to his available stacks. If the pressure on that tile is tied, nothing will happen.
-End of the game: The game ends when at the end of a turn everything on the board is the same as at the end of the previous turn AND 1) Every tile is full OR 2) Both players have 0 stacks available.


It is still a work in progress!
