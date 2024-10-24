package com.site.pochak.app.core.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val pochakDispatcher: PochakDispatchers)

enum class PochakDispatchers {
    Default,
    IO,
}
