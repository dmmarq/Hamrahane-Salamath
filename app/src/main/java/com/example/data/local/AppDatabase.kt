package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Donation
import com.example.data.model.Pledge
import com.example.data.model.UserProfile
import com.example.data.model.VolunteerRegistration
import com.example.data.model.WallMessage

@Database(
    entities = [
        UserProfile::class,
        Donation::class,
        Pledge::class,
        WallMessage::class,
        VolunteerRegistration::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun donationDao(): DonationDao
    abstract fun pledgeDao(): PledgeDao
    abstract fun wallDao(): WallDao
    abstract fun volunteerDao(): VolunteerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hamrahan_salamat_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
