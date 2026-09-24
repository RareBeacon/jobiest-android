package com.jobiest.android

import android.app.Application
import com.jobiest.android.network.NetworkClient
import com.jobiest.android.security.SecureSessionManager

/**
 * Main application class initializing security, network clients, and dependencies.
 */
class JobiestApp : Application() {

    lateinit var sessionManager: SecureSessionManager
        private set

    lateinit var networkClient: NetworkClient
        private set

    lateinit var connectivityObserver: com.jobiest.android.network.NetworkConnectivityObserver
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Android Keystore-backed secure session storage
        sessionManager = SecureSessionManager(this)

        // Initialize HTTPS Retrofit network layer with Bearer Auth interceptor
        networkClient = NetworkClient(sessionManager)

        // Initialize network connectivity observer
        connectivityObserver = com.jobiest.android.network.NetworkConnectivityObserver(this)
    }

    companion object {
        lateinit var instance: JobiestApp
            private set
    }
}
