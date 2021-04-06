package com.gmail.marcosav2010

import io.ktor.locations.*
import kotlin.test.Test

class ApplicationTest {
    @KtorExperimentalLocationsAPI
    @Test
    fun testRoot() {
/*withTestApplication({ module(testing = true) }) {
    handleRequest(HttpMethod.Get, "/").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        assertEquals("HELLO WORLD!", response.content)
    }
}*/
    }
}
