
package com.example.utspraktikumnadila

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.utspraktikumnadila.ui.theme.UTSPraktikumNadilaTheme
import kotlinx.coroutines.launch

class DestinationListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filterStatus = intent.getStringExtra("status_filter") ?: "ALL"

        val database = DestinationDatabase.getDatabase(this)

        setContent {
            UTSPraktikumNadilaTheme {

                val destinations by database.destinationDao()
                    .getAllDestinations()
                    .collectAsState(initial = emptyList())

                val filteredDestinations = when (filterStatus) {
                    "Sudah Dikunjungi" ->
                        destinations.filter { it.status == "Sudah Dikunjungi" }

                    "Wishlist" ->
                        destinations.filter { it.status == "Wishlist" }

                    else -> destinations
                }

                DestinationListScreen(
                    destinations = filteredDestinations,
                    filterStatus = filterStatus,
                    onKembali = {
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun DestinationListScreen(
    destinations: List<DestinationEntity>,
    filterStatus: String,
    onKembali: () -> Unit
) {

    val cream = Color(0xFFFFF9F5)
    val pink = Color(0xFFE8AAB8)
    val darkBrown = Color(0xFF59444A)
    val softPink = Color(0xFFF3E5E7)

    val judul = when (filterStatus) {
        "Sudah Dikunjungi" -> "Tempat yang Pernah Dikunjungi"
        "Wishlist" -> "Daftar Tempat Impian"
        else -> "Semua Destinasi"
    }

    Scaffold(
        containerColor = cream
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {

            Text(
                text = judul,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = darkBrown
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${destinations.size} destinasi tersimpan",
                color = darkBrown
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (destinations.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            softPink,
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada destinasi tersimpan.\nYuk, tambahkan destinasi pertamamu!",
                        color = darkBrown,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = destinations,
                        key = { it.id }
                    ) { destination ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 3.dp
                            )
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                if (destination.gambarUri.isNotBlank()) {
                                    AsyncImage(
                                        model = destination.gambarUri,
                                        contentDescription = "Foto destinasi",
                                        modifier = Modifier
                                            .size(90.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .background(
                                                softPink,
                                                RoundedCornerShape(10.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("📍", fontSize = 28.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = destination.namaTempat,
                                        fontWeight = FontWeight.Bold,
                                        color = darkBrown,
                                        fontSize = 16.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = destination.kategori,
                                        color = darkBrown,
                                        fontSize = 13.sp
                                    )

                                    Text(
                                        text = destination.lokasi,
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = destination.status,
                                        color = pink,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )

                                    if (destination.status == "Sudah Dikunjungi") {
                                        Text(
                                            text = "Tanggal: ${destination.tanggal}",
                                            color = darkBrown,
                                            fontSize = 12.sp
                                        )

                                        destination.rating?.let {
                                            Text(
                                                text = "Rating: $it/5 ⭐",
                                                color = darkBrown,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onKembali,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Kembali",
                    color = darkBrown
                )
            }
        }
    }
}
