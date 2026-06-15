package com.muedsa.tvbox.douyu.service

import com.muedsa.tvbox.douyu.TestPlugin
import com.muedsa.tvbox.douyu.checkMediaCardRows
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenServiceTest {

    private val service = TestPlugin.provideMainScreenService()

    @Test
    fun getRowsDataTest() = runTest{
        val rows = service.getRowsData()
        checkMediaCardRows(rows = rows)
    }

}