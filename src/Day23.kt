import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {

    val costOffsets = arrayOf(
        intArrayOf(3, 2, 0, 2, 0, 4, 0, 6, 0, 8, 9),
        intArrayOf(5, 4, 0, 2, 0, 2, 0, 4, 0, 6, 7),
        intArrayOf(7, 6, 0, 4, 0, 2, 0, 2, 0, 4, 5),
        intArrayOf(9, 8, 0, 6, 0, 4, 0, 2, 0, 2, 3)
    )

    val doorwayPositions = listOf(2, 4, 6, 8)

    val moveCosts = listOf(1, 10, 100, 1000)

    val compareBoardStates =
        compareBy<Pair<BoardState, Int>> { (boardState, estimate) -> boardState.costSoFar + estimate }

    fun organiseAmphipods(initialBoardState: BoardState): Int {

        var bestSoFar = Int.MAX_VALUE

        fun getCostToCompletion(inState: BoardState): Int {
            return inState.hallway.mapIndexed { index, c ->
                if (c == '.') 0
                else {
                    val amphipodType = (c - 'A')
                    (costOffsets[amphipodType][index] + 4) * moveCosts[amphipodType]
                }
            }
                .sum() +
                    inState.rooms.flatMapIndexed { roomsIndex, s ->
                        s.mapIndexed { roomPosition, c ->
                            if (c == '.') 0
                            else {
                                val numberOfRoomsToMove = abs(roomsIndex - (c - 'A'))
                                (numberOfRoomsToMove * 2 + if (numberOfRoomsToMove > 0) 2 * roomPosition + 1 else 0) * moveCosts[c - 'A']
                            }
                        }
                    }.sum()
        }

        fun getCostFromHallway(hallwayPosition: Int, targetRoom: Int, roomIndex: Int): Int {
            return (costOffsets[targetRoom][hallwayPosition] + roomIndex) * moveCosts[targetRoom]
        }

        fun getCostToHallway(hallwayPosition: Int, fromRoom: Int, roomIndex: Int, amphipodType: Char): Int {
            return (costOffsets[fromRoom][hallwayPosition] + roomIndex) * moveCosts[amphipodType - 'A']
        }

        fun getMovesFromRoomsIntoHallway(
            inState: BoardState,
            roomIndex: Int,
            roomPosition: Int,
            amphipod: Char
        ): List<BoardState> {
            val boardStates = mutableListOf<BoardState>()
            val offsets = costOffsets[roomIndex]
            val doorwayPosition = doorwayPositions[roomIndex]
            for (i in doorwayPosition - 1 downTo 0) {
                if (inState.hallway[i] != '.') break
                if (offsets[i] != 0) {
                    val newCost = inState.costSoFar + getCostToHallway(i, roomIndex, roomPosition, amphipod)
                    if (newCost < bestSoFar)
                        boardStates.add(
                            BoardState(
                                newCost,
                                inState.hallway.substring(0, i) + "$amphipod" + inState.hallway.substring(i + 1),
                                inState.rooms.subList(0, roomIndex) +
                                        inState.rooms[roomIndex].replaceFirst(amphipod, '.') +
                                        if (roomIndex < inState.rooms.size)
                                            inState.rooms.subList(roomIndex + 1, inState.rooms.size)
                                        else emptyList(),
                            )
                        )
                }
            }
            for (i in doorwayPosition + 1..10) {
                if (inState.hallway[i] != '.') break
                if (offsets[i] != 0) {
                    val newCost = inState.costSoFar + getCostToHallway(i, roomIndex, roomPosition, amphipod)
                    if (newCost < bestSoFar)
                        boardStates.add(
                            BoardState(
                                newCost,
                                inState.hallway.substring(0, i) + "$amphipod" + inState.hallway.substring(i + 1),
                                inState.rooms.subList(0, roomIndex) +
                                        inState.rooms[roomIndex].replaceFirst(amphipod, '.') +
                                        if (roomIndex < inState.rooms.size)
                                            inState.rooms.subList(roomIndex + 1, inState.rooms.size)
                                        else emptyList(),
                            )
                        )
                }
            }
            return boardStates
        }

        fun getPossibleMoves(inState: BoardState): List<BoardState> {
            val boardStates = mutableListOf<BoardState>()
            val sizeOfRoom = inState.rooms[0].length - 1

            // Amphipods in the hallway can move into their own rooms
            inState.hallway.forEachIndexed { index, amphipod ->
                if (amphipod != '.') {
                    val targetRoom = amphipod - 'A'
                    val targetDoorway = doorwayPositions[targetRoom]
                    val blockCheckIndex = if (targetDoorway < index) index - 1 else index + 1
                    val blockingAmphipodCount =
                        (min(blockCheckIndex, targetDoorway)..max(blockCheckIndex, targetDoorway)).count {
                            inState.hallway[it] != '.'
                        }
                    val blockedRoom = inState.rooms[targetRoom].count { it != '.' && it != amphipod }
                    if (blockingAmphipodCount == 0 && blockedRoom == 0) {
                        val emptySlot = inState.rooms[targetRoom].lastIndexOf('.')
                        val newRoom = inState.rooms[targetRoom].toCharArray()
                        newRoom[emptySlot] = amphipod
                        for (i in sizeOfRoom downTo 0) {
                            if (inState.rooms[targetRoom][i] == '.') {
                                val newCost = inState.costSoFar + getCostFromHallway(index, targetRoom, i)
                                if (newCost < bestSoFar)
                                    boardStates.add(
                                        BoardState(
                                            inState.costSoFar + getCostFromHallway(index, targetRoom, i),// todo
                                            inState.hallway.substring(
                                                0,
                                                index
                                            ) + '.' + inState.hallway.substring(index + 1),
                                            inState.rooms.subList(0, targetRoom) +
                                                    newRoom.joinToString("") +
                                                    inState.rooms.subList(targetRoom + 1, inState.rooms.size),
                                        )
                                    )
                                break
                            }
                        }
                    }
                }
            }

            // Amphipods in rooms (that aren't their own can move into the hallway)
            inState.rooms.forEachIndexed { roomsIndex, room -> //// TODO here
                for (i in 0..sizeOfRoom) {
                    if (room[i] == '.') continue
                    if (room.substring(i).any { it != 'A' + roomsIndex }) {
                        boardStates.addAll(getMovesFromRoomsIntoHallway(inState, roomsIndex, i, room[i]))
                        break
                    }
                }
            }

            return boardStates
        }

        val candidatesQueue = PriorityQueue(compareBoardStates)
        candidatesQueue.offer(Pair(initialBoardState, getCostToCompletion(initialBoardState)))

        while (candidatesQueue.isNotEmpty()) {
            val candidate = candidatesQueue.remove().first
            if (candidate.costSoFar < bestSoFar) {
                val newMoves = getPossibleMoves(candidate)
                newMoves.forEach {
                    val costToCompletion = getCostToCompletion(it)
                    if (costToCompletion == 0) {
                        if (it.costSoFar < bestSoFar) {
                            bestSoFar = it.costSoFar
                        }
                    } else if (costToCompletion + it.costSoFar < bestSoFar) candidatesQueue.offer(
                        Pair(
                            it,
                            costToCompletion
                        )
                    )
                }
            }
        }

        return bestSoFar
    }

    fun part1(initialBoardState: BoardState): Int {
        return organiseAmphipods(initialBoardState)
    }

    fun part2(initialBoardState: BoardState): Int {
        return organiseAmphipods(initialBoardState)
    }

    val sampleBoardState = BoardState(
        costSoFar = 0,
        "...........",
        listOf("BA", "CD", "BC", "DA"),
    )

    val realBoardState = BoardState(
        costSoFar = 0,
        "...........",
        listOf("DD", "CA", "BA", "CB"),
    )

    val bigSampleBoardState = BoardState(
        costSoFar = 0,
        "...........",
        listOf("BDDA", "CCBD", "BBAC", "DACA"),
    )

    val bigRealBoardState = BoardState(
        costSoFar = 0,
        "...........",
        listOf("DDDD", "CCBA", "BBAA", "CACB"),
    )

//    check(part1(sampleBoardState) == 12521)
    println("Part1: ${part1(realBoardState)}")

//    check(part2(bigSampleBoardState) == 44169)
    println("Part2: ${part2(bigRealBoardState)}")
}

private data class BoardState(
    val costSoFar: Int,
    val hallway: String,
    val rooms: List<String>,
)

