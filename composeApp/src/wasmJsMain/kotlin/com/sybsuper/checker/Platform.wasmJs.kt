package com.sybsuper.checker

import kotlinx.browser.window

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()
actual fun fetchUrl(urlPath: String, callBack: (String) -> Unit) {
    window.fetch(getDomain() + urlPath).then { // todo: fix cors
        it.text().then {
            callBack(it.toString())
            null
        }.catch {
            window.alert(it.toString())
            null
        }
    }.catch {
        window.alert(it.toString())
        null
    }
}

actual fun getDomain(): String = window.location.toString()