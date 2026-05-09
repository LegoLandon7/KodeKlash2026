# KodeKlashProject

### By Simon and Landon

---

This README file has sources I made or referenced in this project. Comments heavily explain everything else in the project.

###### Note: some AI was used mainly with google AI overview showing obvious bugs or logic. No code here is directly from AI, and it was only used as a tool.

**Landon worked on:** ray-casting, rendering, entities, player input
\
**Simon worked on:** entity art, main menu art, main menu

## Raycasting

---

main math used for ray-casting - https://lodev.org/cgtutor/raycasting.html
\
how rays are cast (made by me) - https://www.desmos.com/calculator/pc01f7dvw4


Raycasting *casts* rays from a point and calculates the distance of the wall segment, that distance is used to fint the height at any certain point of the wall.

## PathFinding

---

https://en.wikipedia.org/wiki/Breadth-first_search

Pathfinding uses the BFS algorithm (Breadth-First Search) this algorithm basically creates a map of all spaces that point towards a single space, allowing entities to path towards the player.