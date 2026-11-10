
package com.example.utspraktikumnadila

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DestinationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DestinationDatabase : RoomDatabase() {

    abstract fun destinationDao(): DestinationDao

    companion object {

        @Volatile
        private var INSTANCE: DestinationDatabase? = null

        fun getDatabase(context: Context): DestinationDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DestinationDatabase::class.java,
                    "destination_rate_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
