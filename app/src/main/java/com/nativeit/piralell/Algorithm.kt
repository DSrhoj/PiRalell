package com.nativeit.piralell

import android.util.Log
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import java.util.Random
import kotlin.math.pow
import kotlin.math.sqrt

fun CoroutineScope.processCalculation(
    coroutineBlocks: Int,
    pointsForBlock: Int,
    tv: TextView
) {
    val results = Channel<BlockResult>()
    val blockId = Channel<Int>()
    repeat(coroutineBlocks + 1) { computeBlockOfPoints(pointsForBlock, blockId, results) }
    calculatePi(coroutineBlocks, results, blockId, tv)
}

fun CoroutineScope.calculatePi(
    coroutineBlocks: Int,
    results: ReceiveChannel<BlockResult>,
    blockId: SendChannel<Int>,
    tv: TextView
) {
    launch {
        val globalResult = GlobalResult(0, 0)
        for (i in 1..coroutineBlocks) {
//            Log.d("BLOCK_PROCESS", "Block processed is started")
            blockId.send(i)
        }
        while (true) {
            select<Unit> {
                results.onReceive { blockResult ->
                    globalResult.pointsInCircle += blockResult.pointsInCircle
                    globalResult.pointsOutCircle += blockResult.pointsOutCircle
                    launch(Dispatchers.Main) {
//                        delay(1000)
                        tv.text = ((globalResult.pointsInCircle.toFloat() * 4)/globalResult.sum()).toString()
                    }
//                    Log.d("BLOCK_PROCESS", "${blockResult.id} Block is processed")
//                    tv.text = ((globalResult.pointsInCircle.toFloat() * 4)/globalResult.sum()).toString()
                }
            }
        }
    }
}

fun CoroutineScope.computeBlockOfPoints(
    blockSize: Int,
    blockId: ReceiveChannel<Int>,
    results: SendChannel<BlockResult>
) {
    launch {
        for (id in blockId) {
            var pointsInCircle = 0
            var pointsOutCircle = 0
            // this loop so that every coroutine does fair amount of work
            // not big deal if it fails, just wont take that points in consideration
            for (j in 0..blockSize) {
                val x = (Random().nextFloat() * 2) - 1
                val y = (Random().nextFloat() * 2) - 1

//                Log.d("BLOCK_PROCESS", "X: $x Y: $y")

                val distFormCenter = sqrt(x.pow(2) + y.pow(2))

                if (distFormCenter < 1) {
                    pointsInCircle++
                } else {
                    pointsOutCircle++
                }
//                Log.d("BLOCK_PROCESS", "Distance: $distFormCenter")
            }
//            Log.d("BLOCK_PROCESS", "Block processed is in progress")
//            Log.d("BLOCK_PROCESS", "In: $pointsInCircle Out: $pointsOutCircle")
            results.send(BlockResult(id, pointsInCircle, pointsOutCircle))
        }
    }
}

data class BlockResult(val id: Int, val pointsInCircle: Int, val pointsOutCircle: Int)
data class GlobalResult(var pointsInCircle: Int, var pointsOutCircle: Int) {
    fun sum(): Float {
        return (pointsInCircle + pointsOutCircle).toFloat()
    }
}