package com.jobiest.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jobiest.android.security.Session
import com.jobiest.android.ui.navigation.JobiestNavGraph
import com.jobiest.android.ui.navigation.Screen
import com.jobiest.android.ui.theme.JobiestBg
import com.jobiest.android.ui.theme.JobiestTheme

/**
 * Single activity container running Jetpack Compose and handling OAuth & Notification deep links.
 */
class MainActivity : ComponentActivity() {

    private var activeNavController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            val navController = rememberNavController()
            activeNavController = navController

            JobiestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = JobiestBg
                ) {
                    JobiestNavGraph(navController = navController)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val uri = intent?.data ?: return

        // 1. OAuth Callback: jobiest://auth/callback#access_token=...
        if (uri.scheme == "jobiest" && uri.host == "auth" && uri.path == "/callback") {
            extractTokensFromUri(uri)
            return
        }

        // 2. Notification Deep Links: jobiest://applications, jobiest://jobs, etc.
        if (uri.scheme == "jobiest") {
            val destination = when (uri.host) {
                "applications" -> Screen.Applications.route
                "jobs" -> Screen.Jobs.route
                "resume" -> Screen.ResumeStudio.route
                "agent" -> Screen.Agent.route
                "profile" -> Screen.Profile.route
                "notifications" -> Screen.NotificationSettings.route
                else -> null
            }
            destination?.let { route ->
                activeNavController?.navigate(route) {
                    launchSingleTop = true
                }
            }
        }
    }

    private fun extractTokensFromUri(uri: Uri) {
        val fragment = uri.fragment
        if (!fragment.isNullOrBlank()) {
            val params = fragment.split("&").associate {
                val parts = it.split("=")
                if (parts.size == 2) parts[0] to Uri.decode(parts[1]) else "" to ""
            }
            val accessToken = params["access_token"]
            val refreshToken = params["refresh_token"]
            val expiresIn = params["expires_in"]?.toLongOrNull() ?: 3600L

            if (!accessToken.isNullOrBlank()) {
                val session = Session(
                    accessToken = accessToken,
                    refreshToken = refreshToken ?: "",
                    userId = "",
                    email = "",
                    expiresAt = System.currentTimeMillis() + (expiresIn * 1000)
                )
                JobiestApp.instance.sessionManager.saveSession(session)
            }
        }
    }
}
