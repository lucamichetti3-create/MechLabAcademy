package it.lucamichetti.mechlabacademy

import android.app.Application
import androidx.work.Configuration
import it.lucamichetti.mechlabacademy.data.AppContainer

class MechLabApplication : Application(), Configuration.Provider {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setMinimumLoggingLevel(android.util.Log.INFO).build()
}
