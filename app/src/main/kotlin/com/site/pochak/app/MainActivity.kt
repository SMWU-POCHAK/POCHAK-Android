package com.site.pochak.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.site.pochak.app.core.designsystem.theme.PochakTheme
import com.site.pochak.app.ui.PochakApp
import com.site.pochak.app.ui.rememberPochakAppState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        /**
         * Splash Screen 유지 옵션
         * 유저 데이터, 로그인 데이터 로딩 등
         *
         * @return true: 화면 유지, false: 화면 제거
         */
        splashScreen.setKeepOnScreenCondition {
            false
        }

        enableEdgeToEdge()

        setContent {
            val appState = rememberPochakAppState()

            PochakTheme {
                PochakApp(appState)
            }
        }
    }
}
