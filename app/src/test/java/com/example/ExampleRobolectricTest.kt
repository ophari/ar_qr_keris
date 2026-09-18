package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.qr.KerisQrCatalog
import com.example.qr.SimpleQrGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Keris AR Scanner", appName)
    }

    @Test
    fun `keris qr catalog contains standard targets`() {
        assertTrue(KerisQrCatalog.targets.isNotEmpty())
        val luk9 = KerisQrCatalog.findTarget("KERIS_LUK_9")
        assertNotNull(luk9)
        assertEquals(9, luk9?.lukCount)
    }

    @Test
    fun `simple qr generator generates bitmap`() {
        val qrBitmap = SimpleQrGenerator.generateQrBitmap("KERIS_LUK_9", 100)
        assertNotNull(qrBitmap)
        assertEquals(100, qrBitmap.width)
        assertEquals(100, qrBitmap.height)
    }
}
