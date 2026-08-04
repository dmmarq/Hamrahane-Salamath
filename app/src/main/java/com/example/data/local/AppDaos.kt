package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Donation
import com.example.data.model.Pledge
import com.example.data.model.UserProfile
import com.example.data.model.VolunteerRegistration
import com.example.data.model.WallMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserProfile)

    @Update
    suspend fun updateUser(user: UserProfile)
}

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations ORDER BY timestamp DESC")
    fun getAllDonations(): Flow<List<Donation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: Donation): Long

    @Query("SELECT SUM(amount) FROM donations")
    fun getTotalDonated(): Flow<Long?>

    @Query("SELECT COUNT(*) FROM donations")
    fun getDonationCount(): Flow<Int>
}

@Dao
interface PledgeDao {
    @Query("SELECT * FROM pledges ORDER BY id DESC")
    fun getAllPledges(): Flow<List<Pledge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPledge(pledge: Pledge): Long

    @Query("DELETE FROM pledges WHERE id = :id")
    suspend fun deletePledge(id: Long)
}

@Dao
interface WallDao {
    @Query("SELECT * FROM wall_messages WHERE isApproved = 1 ORDER BY id DESC")
    fun getAllWallMessages(): Flow<List<WallMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallMessage(message: WallMessage): Long
}

@Dao
interface VolunteerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteer(volunteer: VolunteerRegistration): Long

    @Query("SELECT * FROM volunteers ORDER BY id DESC")
    fun getAllVolunteers(): Flow<List<VolunteerRegistration>>
}
