package com.example.utspraktikumnadila

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DestinationDao {

    // Menambahkan destinasi
    @Insert
    suspend fun insertDestination(
        destination: DestinationEntity
    )

    // Mengambil semua destinasi
    @Query("SELECT * FROM destinasi ORDER BY id DESC")
    fun getAllDestinations(): Flow<List<DestinationEntity>>

    // Mengambil destinasi berdasarkan ID
    @Query("SELECT * FROM destinasi WHERE id = :id")
    suspend fun getDestinationById(
        id: Int
    ): DestinationEntity?

    // Mengubah data destinasi
    @Update
    suspend fun updateDestination(
        destination: DestinationEntity
    )

    // Menghapus data destinasi
    @Delete
    suspend fun deleteDestination(
        destination: DestinationEntity
    )
}
