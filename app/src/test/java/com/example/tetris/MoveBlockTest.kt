package com.example.tetris

import com.example.tetris.Utilities.Companion.addWallToBlocks
import com.example.tetris.Utilities.Companion.insertBlock
import com.example.tetris.block.BlockInterface
import com.example.tetris.block.InitBlock
import com.example.tetris.block.StraightBlock
import com.example.tetris.model.Field
import org.junit.Test
import org.junit.Assert.*
import org.junit.Rule
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import kotlin.random.Random

class MoveBlockTest : KoinTest {
  @get:Rule
  val koinTestRule = KoinTestRule.create {
    modules(
      module {
        single { (
                   selectedBlocks: BlockInterface,
                   blocks: Array<Array<Int>>,
                   nextBlocks: MutableList<Int>,
                   heldBlock: Int,
                   random: Random
                 ) ->
          Field(
            selectedBlocks,
            blocks,
            nextBlocks,
            heldBlock,
            random
          )
        }
      }
    )
  }

  @Test
  fun moveBlock() {
    val field: Field by inject {
      parametersOf(
        InitBlock(),
        Array(22) { Array<Int>(12) { 0 } },
        mutableListOf(0, 0, 0),
        0,
        Random
      )
    }
    field.addBlock()
    field.moveBlock()
    val expectedBlocks: Array<Array<Int>> = Array(22) { Array<Int>(12) { 0 } }
    expectedBlocks.apply {
      insertBlock(
        expectedBlocks,
        field.selectedBlock.type,
        5,
        1
      )
      addWallToBlocks(expectedBlocks)
    }
    assertArrayEquals(expectedBlocks, field.combineBlocks())
  }

  @Test
  fun canMoveBlock() {
    val field: Field by inject {
      parametersOf(
        InitBlock(),
        Array(22) { Array<Int>(12) { 0 } },
        mutableListOf(0, 0, 0),
        0,
        Random
      )
    }
    assertTrue(field.checkMoveBlock())
  }

  @Test
  fun cantMoveBlockWithCollapse() {
    val blocks: Array<Array<Int>> = Array(22) { Array<Int>(12) { 0 } }
    blocks.apply {
      insertBlock(
        blocks,
        1,
        5,
        1
      )
    }
    val field: Field by inject {
      parametersOf(
        StraightBlock(),
        blocks,
        mutableListOf(0, 0, 0),
        0,
        Random
      )
    }
    assertFalse(field.checkMoveBlock())
  }

  @Test
  fun cantMoveBlockWithBottom() {
    val field: Field by inject {
      parametersOf(
        StraightBlock(
          arrayOf(
            arrayOf(5, 20),
            arrayOf(6, 20),
            arrayOf(7, 20),
            arrayOf(8, 20)
          )
        ),
        Array(22) { Array<Int>(12) { 0 } },
        mutableListOf(0, 0, 0),
        0,
        Random
      )
    }
    assertFalse(field.checkMoveBlock())
  }

  @Test
  fun moveBlockToBottom() {
    val field: Field by inject {
      parametersOf(
        InitBlock(),
        Array(22) { Array<Int>(12) { 0 } },
        mutableListOf(0, 0, 0),
        0,
        Random
      )
    }
    field.addBlock()
    while (field.checkMoveBlock()) {
      field.moveBlock()
    }
    val expectedBlocks: Array<Array<Int>> = Array(22) { Array<Int>(12) { 0 } }
    expectedBlocks.apply {
      insertBlock(
        expectedBlocks,
        field.selectedBlock.type,
        5,
        if (field.selectedBlock.type == 1) 20 else 19
      )
      addWallToBlocks(expectedBlocks)
    }
    assertArrayEquals(expectedBlocks, field.combineBlocks())
  }
}
