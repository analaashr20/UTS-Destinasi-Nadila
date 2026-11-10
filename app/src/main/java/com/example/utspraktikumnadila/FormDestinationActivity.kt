
package com.example.utspraktikumnadila

import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.utspraktikumnadila.ui.theme.UTSPraktikumNadilaTheme
import kotlinx.coroutines.launch

class FormDestinationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = DestinationDatabase.getDatabase(this)
        val destinationId = intent.getIntExtra("destination_id", -1)

        setContent {
            UTSPraktikumNadilaTheme {

                var dataAwal by remember {
                    mutableStateOf<DestinationEntity?>(null)
                }

                var selesaiMemuat by remember {
                    mutableStateOf(destinationId == -1)
                }

                LaunchedEffect(destinationId) {
                    if (destinationId != -1) {
                        dataAwal = database.destinationDao()
                            .getDestinationById(destinationId)
                    }
                    selesaiMemuat = true
                }

                when {
                    !selesaiMemuat -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    destinationId != -1 && dataAwal == null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Data destinasi tidak ditemukan.")
                            Button(onClick = { finish() }) {
                                Text("Kembali")
                            }
                        }
                    }

                    else -> {
                        FormDestination(
                            dataAwal = dataAwal,

                            onKembali = {
                                finish()
                            },

                            onSimpan = {
                                    namaTempat,
                                    kategori,
                                    lokasi,
                                    status,
                                    tanggal,
                                    rating,
                                    ulasan,
                                    gambarUri ->

                                lifecycleScope.launch {

                                    val destination = DestinationEntity(
                                        id = dataAwal?.id ?: 0,
                                        namaTempat = namaTempat,
                                        kategori = kategori,
                                        lokasi = lokasi,
                                        status = status,
                                        tanggal = tanggal,
                                        rating = rating.toIntOrNull(),
                                        ulasan = ulasan,
                                        gambarUri = gambarUri
                                    )

                                    if (dataAwal == null) {
                                        database.destinationDao()
                                            .insertDestination(destination)
                                    } else {
                                        database.destinationDao()
                                            .updateDestination(destination)
                                    }

                                    finish()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
