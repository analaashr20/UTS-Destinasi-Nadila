
package com.example.utspraktikumnadila

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.utspraktikumnadila.ui.theme.UTSPraktikumNadilaTheme
import kotlinx.coroutines.launch

class DestinationDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val destinationId = intent.getIntExtra("destination_id", -1)
        val database = DestinationDatabase.getDatabase(this)

        setContent {
            UTSPraktikumNadilaTheme {

                var destination by remember {
                    mutableStateOf<DestinationEntity?>(null)
                }

                var isLoading by remember {
                    mutableStateOf(true)
                }

                LaunchedEffect(destinationId) {
                    destination = if (destinationId != -1) {
                        database.destinationDao()
                            .getDestinationById(destinationId)
                    } else {
                        null
                    }
                    isLoading = false
                }

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    destination == null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Destinasi tidak ditemukan.")
                                Button(onClick = { finish() }) {
                                    Text("Kembali")
                                }
                            }
                        }
                    }

                    else -> {
                        DestinationDetailScreen(
                            destination = destination!!,

                            onKembali = {
                                finish()
                            },

                            onEdit = {
                                val intent = Intent(
                                    this,
                                    FormDestinationActivity::class.java
                                )

                                intent.putExtra(
                                    "destination_id",
                                    destination!!.id
                                )

                                startActivity(intent)
                                finish()
                            },

                            onHapus = {
                                lifecycleScope.launch {
                                    database.destinationDao()
                                        .deleteDestination(destination!!)

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

@Composable
fun DestinationDetailScreen(
    destination: DestinationEntity,
    onKembali: () -> Unit,
    onEdit: () -> Unit,
    onHapus: () -> Unit
) {

    val cream = Color(0xFFFFF9F5)
    val pink = Color(0xFFE8AAB8)
    val darkBrown = Color(0xFF59444A)
    val softPink = Color(0xFFF3E5E7)

    var tampilkanKonfirmasi by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cream)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "Detail Destinasi",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = darkBrown
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (destination.gambarUri.isNotBlank()) {
            AsyncImage(
                model = destination.gambarUri,
                contentDescription = "Foto ${destination.namaTempat}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        softPink,
                        RoundedCornerShape(16.dp)
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        softPink,
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("📍", fontSize = 55.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            destination.namaTempat,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = darkBrown
        )

        Spacer(modifier = Modifier.height(12.dp))

        DetailItem("Kategori", destination.kategori)
        DetailItem("Lokasi", destination.lokasi)
        DetailItem("Status", destination.status)

        if (destination.status == "Sudah Dikunjungi") {
            DetailItem("Tanggal Kunjungan", destination.tanggal)
            DetailItem(
                "Rating",
                "${destination.rating ?: "-"} / 5 ⭐"
            )
            DetailItem(
                "Ulasan",
                destination.ulasan.ifBlank {
                    "Belum ada ulasan"
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = pink
            )
        ) {
            Text("Edit Destinasi", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                tampilkanKonfirmasi = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB85C68)
            )
        ) {
            Text("Hapus Destinasi", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onKembali,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kembali", color = darkBrown)
        }
    }

    if (tampilkanKonfirmasi) {
        AlertDialog(
            onDismissRequest = {
                tampilkanKonfirmasi = false
            },
            title = {
                Text(
                    "Hapus Destinasi?",
                    color = darkBrown,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Apakah kamu yakin ingin menghapus ${destination.namaTempat}? Data yang dihapus tidak dapat dikembalikan.",
                    color = darkBrown
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        tampilkanKonfirmasi = false
                        onHapus()
                    }
                ) {
                    Text(
                        "Ya, Hapus",
                        color = Color(0xFFB85C68)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        tampilkanKonfirmasi = false
                    }
                ) {
                    Text("Batal", color = darkBrown)
                }
            },
            containerColor = cream,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        )
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String
) {

    val darkBrown = Color(0xFF59444A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                label,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = darkBrown
            )
        }
    }
}
