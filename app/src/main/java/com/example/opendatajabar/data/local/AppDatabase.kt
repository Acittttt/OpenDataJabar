package com.example.opendatajabar.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// MIGRASI 1 -> 2: Menambah kolom age ke profile_table
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Log.d("DatabaseMigration", "Migrating from version 1 to 2")
        database.execSQL("ALTER TABLE profile_table ADD COLUMN age INTEGER DEFAULT 0 NOT NULL")
    }
}

// MIGRASI 2 -> 3: Menambah kolom profileImageUri ke profile_table
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Log.d("DatabaseMigration", "Migrating from version 2 to 3")
        database.execSQL("ALTER TABLE profile_table ADD COLUMN profileImageUri TEXT")
    }
}

@Database(entities = [DataEntity::class, ProfileEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dataDao(): DataDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Tambahkan migrasi
                    .fallbackToDestructiveMigration() // Untuk debugging, hapus di produksi
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}