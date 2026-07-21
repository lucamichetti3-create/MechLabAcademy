package it.lucamichetti.mechlabacademy.data

import android.content.Context
import androidx.room.Room
import it.lucamichetti.mechlabacademy.data.local.AcademyDatabase
import it.lucamichetti.mechlabacademy.data.preferences.SettingsRepository
import it.lucamichetti.mechlabacademy.data.repository.AcademyRepository
import it.lucamichetti.mechlabacademy.data.seed.SeedImporter
import it.lucamichetti.mechlabacademy.worker.ReminderScheduler

class AppContainer(context: Context) {
    val database: AcademyDatabase = Room.databaseBuilder(
        context.applicationContext,
        AcademyDatabase::class.java,
        "mechlab_academy.db"
    ).addMigrations(*AcademyDatabase.ALL_MIGRATIONS).build()

    val settings = SettingsRepository(context.applicationContext)
    val repository = AcademyRepository(database.academyDao())
    val seedImporter = SeedImporter(context.applicationContext, database)
    val backupManager = BackupManager(context.applicationContext, database)
    val reminderScheduler = ReminderScheduler(context.applicationContext)
}
