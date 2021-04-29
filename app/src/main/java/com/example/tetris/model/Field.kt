package com.example.tetris.model

import com.example.tetris.block.*
import kotlin.random.Random

class Field(
  var selectedBlock: BlockInterface = StraightBlock(),
  val blocks: Array<Array<Int>> =
    Array(22) { Array<Int>(12) { 0 } },
  val nextBlocks: MutableList<Int> = mutableListOf(0, 0, 0),
  var heldBlock: Int = 0,
  private val random: Random = Random
) {
  init {
    addWallToBlocks()
    initNextBlocks()
  }

  private fun addWallToBlocks() {
    for (n in 0..21) {
      blocks[n][0] = -1
      blocks[n][11] = -1
    }
    for (i in 2..10) blocks[21][i] = -1
  }

  private fun initNextBlocks() {
    for (index in nextBlocks.indices) {
      nextBlocks[index] = random.nextInt(1, 7)
    }
  }

  fun addBlock() {
    blocks.apply { combineBlocks() }
    selectedBlock = when (nextBlocks.elementAt(0)) {
      1 -> StraightBlock()
      2 -> SquareBlock()
      3 -> ZBlock()
      4 -> ZReverseBlock()
      5 -> LBlock()
      6 -> LReverseBlock()
      7 -> TBlock()
      else -> SquareBlock()
    }
    nextBlocks.removeAt(0)
    nextBlocks.add(random.nextInt(1, 7))
  }

  fun combineBlocks(): Array<Array<Int>> {
    val combinedBlocks = blocks
    selectedBlock.coordinates.forEach {
      combinedBlocks[it[1]][it[0]] = selectedBlock.type
    }
    return combinedBlocks
  }

  fun moveBlock() {
    if (checkMoveBlock()) {
      for ((index, elem) in selectedBlock.coordinates.withIndex()) {
        selectedBlock.coordinates[index] = arrayOf(elem[0], elem[1]++)
      }
    }
  }

  private fun checkMoveBlock(): Boolean {
    selectedBlock.coordinates.forEach {
      if (blocks[it[1]++][it[0]] != 0) {
        return false
      }
    }
    return true
  }

  fun checkErasableBlock() {
  }

  fun checkGameover() {
  }
}