package com.sybsuper.checker

import java.net.URL

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getDomain(): String = "https://ics-websites.science.uu.nl/docs/vakken/opt/"

actual fun fetchUrl(urlPath: String, callBack: (String) -> Unit) {
    val content = URL(getDomain() + urlPath).readText()
    callBack(content)
}
actual fun getPlatform(): Platform = JVMPlatform()