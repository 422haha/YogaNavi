package com.ssafy.yoganavi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.CountDownLatch

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun sharedFlowWithCollect() {
        val sharedFlow = MutableSharedFlow<Int>()
        val latch = CountDownLatch(10)

        // 2초동안 수집
        CoroutineScope(Dispatchers.Default).launch {
            sharedFlow.collect {
                delay(2000)
                println(it)
                latch.countDown()
            }
        }

        // 1초에 한번씩 데이터 방출
        CoroutineScope(Dispatchers.Default).launch {
            repeat(10) {
                delay(1000)
                sharedFlow.emit(it)
            }
        }

        latch.await()
    }

    @Test
    fun sharedFlowWithCollectLatest() {
        val sharedFlow = MutableSharedFlow<Int>()
        val latch = CountDownLatch(1)

        // 2초동안 수집
        CoroutineScope(Dispatchers.Default).launch {
            sharedFlow.collectLatest {
                delay(2000)
                println(it)
                latch.countDown()
            }
        }

        // 1초에 한번씩 데이터 방출
        CoroutineScope(Dispatchers.Default).launch {
            repeat(10) {
                delay(1000)
                sharedFlow.emit(it)
            }
        }

        latch.await()
    }
}