By Simon and Landon

- open in notepad for best view

this project is made public at https://github.com/LegoLandon7/KodeKlash2026

This README file has sources I made or referenced in this project. Comments heavily explain everything else in the project.
Note: some AI was used mainly with Google AI overview showing obvious bugs, logic, or simple tasks. No code here is directly from AI, and it was only used as a tool.

Landon worked on:** ray-casting, rendering, entities, player input
Simon worked on:** entity art, main menu art, main menu

HOW OUR PROJECT WORKS - Landon Lego | last updated 5/12/26

Raycasting is a technique where an individual point "casts" rays along its field of view, to map out its surrounding structures, these structures in this game are simple tiles, either 1 for being a wall or 0 being a floor.

Each ray is cast from the player, starting from the left side of the field of view, and since my game is grid-based then I can move the ray along the grid lines instead of by a fixed step, making the edges look straighter (because sometimes rays could go inside a tile) and making the game run faster.

The player might not be on a grid-line to start, so the first action a ray takes is to get to the next gridline, this is called the delta distance. Once this delta distance is calculated then the ray can move to the first grid line.

After that, each gridline is a simple 1 unit step away for each direction, so if you are on the x-axis gridline then you need to go the y-side-distance that was calculated, then now you are on the y-axis gridline so you repeat until the ray is on a gridline where a tile is.

Once you get this collision, you can record the distance to the player and if it hit on an x or y side, then move on to the next ray.

Rendering is a little more complicated, if you act like the left side of the field of view is the left side of the screen then you can map each ray to a vertical column of the screens pixels. And as even real-life shows us, things get smaller as they are further. So using the distance of the ray at that column, you can compute the height of the column making it smaller the further it is.

Rendering walls is easy, rendering entities is where it gets hard. each entity has a texture / pixel data. For every entity you must calculate the relationship from the players angle to the angle the entity appears to place it correctly horizontally. For example you can be facing perfectly east while the entity is slightly north-east, this means the entity must be placed slightly to the left of the players field of view.

Rendering entities is similar in walls in that its smaller the further it is, rendering each column, and comparing the distance of the player to the entity can determine is the wall is closer or the entity, if the wall is closer at that certain column then the entity will appear partially behind.

Rendering the healthbars is very similar, its rendered for each column, either red or green based on the percentage of health left compared to the width of the healthbar. The healthbar rendering is done in the same loop as the entity so it also will appear behind walls correctly.

Entities must also be sorted, instead of using Arrays.sort() it is better to use an insertion sort algorithm to sort then descending by distance (so the farthest are rendered first) the insertion sort algorithm works fastest when data is close together, and entities won't move much between a single frame.

Entities also pathfind towards you using the BFS algorithm. This algorithm puts each tile into a queue around the player, then if its a valid tile, then you can mark the direction of that tile to the player, repeating this for every tile on the screen you can have the closest path from any point to the player.

This covers most of the complicated things but dumbed down a lot. I used many sources as well as my own knowledge, and a small amount of AI was used for simple things such as how to read files from a resource folder, how to change font size, how to double buffer, etc. No code is directly from AI. Also stack overflow helps a lot.

Sources used:

https://lodev.org/cgtutor/raycasting.html
https://www.geeksforgeeks.org/dsa/breadth-first-search-or-bfs-for-a-graph/
https://en.wikipedia.org/wiki/Breadth-first_search
https://www.geeksforgeeks.org/java/java-awt-canvas-class/
https://stackoverflow.com/questions/2416935/how-to-play-wav-files-with-java
https://pixabay.com/sound-effects/