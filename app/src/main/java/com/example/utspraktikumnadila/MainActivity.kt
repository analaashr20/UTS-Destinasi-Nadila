
package com.example.utspraktikumnadila

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.utspraktikumnadila.ui.theme.UTSPraktikumNadilaTheme

private val softPink = Color(0xFFF4DCE3)
private val cream = Color(0xFFFFFAF5)
private val darkBrown = Color(0xFF624A4A)
private val mediumPink = Color(0xFFE8B9C8)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = DestinationDatabase
            .getDatabase(applicationContext)
            .destinationDao()

        setContent {
            UTSPraktikumNadilaTheme {

                val destinations by dao.getAllDestinations()
                    .collectAsState(initial = emptyList())

                DestinationHome(
                    destinations = destinations,

                    onTambah = {
                        val intent = Intent(
                            this,
                            FormDestinationActivity::class.java
                        )
                        startActivity(intent)
                    },

                    onLihatSemua = {
                        val intent = Intent(
                            this,
                            DestinationListActivity::class.java
                        )
                        intent.putExtra("status_filter", "Semua")
                        startActivity(intent)
                    },

                    onLihatWishlist = {
                        val intent = Intent(
                            this,
                            DestinationListActivity::class.java
                        )
                        intent.putExtra("status_filter", "Wishlist")
                        startActivity(intent)
                    },

                    onLihatDikunjungi = {
                        val intent = Intent(
                            this,
                            DestinationListActivity::class.java
                        )
                        intent.putExtra(
                            "status_filter",
                            "Sudah Dikunjungi"
                        )
                        startActivity(intent)
                    },

                    onDetailDestinasi = { id ->
                        val intent = Intent(
                            this,
                            DestinationDetailActivity::class.java
                        )
                        intent.putExtra("destination_id", id)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun DestinationHome(
    destinations: List<DestinationEntity>,
    onTambah: () -> Unit,
    onLihatSemua: () -> Unit,
    onLihatWishlist: () -> Unit,
    onLihatDikunjungi: () -> Unit,
    onDetailDestinasi: (Int) -> Unit
) {

    val jumlahSemua = destinations.size

    val jumlahWishlist = destinations.count {
        it.status == "Wishlist"
    }

    val jumlahDikunjungi = destinations.count {
        it.status == "Sudah Dikunjungi"
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = cream
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {

                Text(
                    text = "Destination Rate",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBrown
                )

                Text(
                    text = "Jurnal Destinasi Pribadimu",
                    fontSize = 14.sp,
                    color = darkBrown
                )

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(
                        id = R.drawable.ic_destination
                    ),
                    contentDescription = "Ilustrasi destinasi",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Catat setiap perjalanan,\nsimpan setiap kenangan.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = darkBrown
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onTambah,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = mediumPink
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "+ Tambah Destinasi",
                        color = darkBrown,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Ringkasan Destinasi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBrown,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    SummaryCard(
                        title = "Semua",
                        count = jumlahSemua,
                        modifier = Modifier.weight(1f),
                        onClick = onLihatSemua
                    )

                    SummaryCard(
                        title = "Wishlist",
                        count = jumlahWishlist,
                        modifier = Modifier.weight(1f),
                        onClick = onLihatWishlist
                    )

                    SummaryCard(
                        title = "Dikunjungi",
                        count = jumlahDikunjungi,
                        modifier = Modifier.weight(1f),
                        onClick = onLihatDikunjungi
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Destinasi Terbaru",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBrown,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (destinations.isEmpty()) {

                item {
                    Text(
                        text = "Belum ada destinasi.\nYuk, tambahkan tempat impianmu!",
                        color = darkBrown,
                        modifier = Modifier.padding(20.dp)
                    )
                }

            } else {

                items(destinations.take(5)) { destination ->

                    DestinationCard(
                        destination = destination,
                        onClick = {
                            onDetailDestinasi(destination.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    count: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier.clickable {
            onClick()
        },
        colors = CardDefaults.cardColors(
            containerColor = softPink
        ),
        shape = RoundedCornerShape(14.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = count.toString(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = darkBrown
            )

            Text(
                text = title,
                fontSize = 12.sp,
                color = darkBrown
            )
        }
    }
}

@Composable
fun DestinationCard(
    destination: DestinationEntity,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp)
    ) {

        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Preview foto destinasi
            if (destination.gambarUri.isNotBlank()) {

                AsyncImage(
                    model = destination.gambarUri,
                    contentDescription = "Foto destinasi",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )

            } else {

                // Tampilan jika belum ada foto
                Surface(
                    modifier = Modifier.size(90.dp),
                    color = softPink,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Photo",
                            fontSize = 11.sp,
                            color = darkBrown
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = destination.namaTempat,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBrown
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = destination.kategori,
                    color = darkBrown
                )

                Text(
                    text = destination.lokasi,
                    color = darkBrown
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = destination.status,
                    color = Color(0xFFAD7186),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
