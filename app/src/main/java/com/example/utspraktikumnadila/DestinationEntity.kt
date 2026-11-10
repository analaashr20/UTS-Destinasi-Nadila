package com.example.utspraktikumnadila

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "destinasi")
data class DestinationEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val namaTempat: String,
    val kategori: String,
    val lokasi: String,
    val status: String,
    val tanggal: String = "",
    val rating: Int? = null,
    val ulasan: String = "",
    val gambarUri: String = ""
)
