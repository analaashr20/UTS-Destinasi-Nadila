
package com.example.utspraktikumnadila

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDestination(
    dataAwal: DestinationEntity? = null,
    onKembali: () -> Unit = {},
    onSimpan: (
        String, String, String, String,
        String, String, String, String
    ) -> Unit = { _, _, _, _, _, _, _, _ -> }
) {

    var namaTempat by rememberSaveable(dataAwal?.id) {
        mutableStateOf(dataAwal?.namaTempat ?: "")
    }
    var lokasi by rememberSaveable(dataAwal?.id) {
        mutableStateOf(dataAwal?.lokasi ?: "")
    }
    var kategori by rememberSaveable(dataAwal?.id) {
        mutableStateOf(dataAwal?.kategori ?: "Wisata")
    }
    var status by rememberSaveable(dataAwal?.id) {
        mutableStateOf(dataAwal?.status ?: "Wishlist")
    }
    var tanggal by rememberSaveable(dataAwal?.id) {
        mutableStateOf(dataAwal?.tanggal ?: "")
    }
    var rating by rememberSaveable(dataAwal?.id) {
        mutableStateOf(dataAwal?.rating?.toString() ?: "")
    }
    var ulasan by rememberSaveable(dataAwal?.id) {
        mutableStateOf(dataAwal?.ulasan ?: "")
    }
    var gambarUri by rememberSaveable(dataAwal?.id) {
        mutableStateOf(dataAwal?.gambarUri ?: "")
    }
    var pesanError by rememberSaveable {
        mutableStateOf("")
    }

    var tampilkanKalender by rememberSaveable {
        mutableStateOf(false)
    }

    val pink = Color(0xFFE8AAB8)
    val cream = Color(0xFFFFF9F5)
    val darkBrown = Color(0xFF59444A)
    val softPink = Color(0xFFF3E5E7)

    val context = LocalContext.current

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = darkBrown,
        unfocusedLabelColor = darkBrown,
        cursorColor = pink,
        focusedBorderColor = pink,
        unfocusedBorderColor = Color(0xFFD8C2C6)
    )

    fun tanggalHariIni(): String {
        return SimpleDateFormat(
            "dd MMMM yyyy",
            Locale("id", "ID")
        ).format(Date())
    }

    val launcherGambar = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            gambarUri = uri.toString()

            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                pesanError = "Izin akses foto tidak dapat dipertahankan."
            }
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cream)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = if (dataAwal == null) "Tambah Destinasi" else "Edit Destinasi",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = darkBrown
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (dataAwal == null)
                "Catat tempat favorit atau destinasi impianmu!"
            else
                "Perbarui informasi destinasi pilihanmu.",
            color = darkBrown
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Foto Destinasi",
            fontWeight = FontWeight.SemiBold,
            color = darkBrown
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (gambarUri.isNotBlank()) {
            AsyncImage(
                model = gambarUri,
                contentDescription = "Foto destinasi",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(softPink),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada foto dipilih", color = darkBrown)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                launcherGambar.launch(arrayOf("image/*"))
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = pink
            )
        ) {
            Text("Pilih Foto dari Galeri", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = namaTempat,
            onValueChange = { namaTempat = it },
            label = { Text("Nama Tempat") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = fieldColors
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = lokasi,
            onValueChange = { lokasi = it },
            label = { Text("Lokasi") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = fieldColors
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Kategori Destinasi",
            fontWeight = FontWeight.SemiBold,
            color = darkBrown
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Wisata", "Hotel", "Tempat Makan").forEach { item ->
                FilterChip(
                    selected = kategori == item,
                    onClick = { kategori = item },
                    label = { Text(item) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = pink,
                        selectedLabelColor = Color.White,
                        containerColor = softPink,
                        labelColor = darkBrown
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = kategori == item,
                        borderColor = Color(0xFFD8C2C6),
                        selectedBorderColor = pink
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Status Destinasi",
            fontWeight = FontWeight.SemiBold,
            color = darkBrown
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Wishlist", "Sudah Dikunjungi").forEach { item ->
                FilterChip(
                    selected = status == item,
                    onClick = {
                        status = item

                        if (item == "Sudah Dikunjungi" && tanggal.isBlank()) {
                            tanggal = tanggalHariIni()
                        }
                    },
                    label = { Text(item) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = pink,
                        selectedLabelColor = Color.White,
                        containerColor = softPink,
                        labelColor = darkBrown
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = status == item,
                        borderColor = Color(0xFFD8C2C6),
                        selectedBorderColor = pink
                    )
                )
            }
        }

        if (status == "Sudah Dikunjungi") {

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = tanggal,
                onValueChange = {},
                label = { Text("Tanggal Kunjungan") },
                placeholder = { Text("Pilih tanggal kunjungan") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    TextButton(
                        onClick = { tampilkanKalender = true }
                    ) {
                        Text("📅", fontSize = 20.sp)
                    }
                },
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = rating,
                onValueChange = {
                    if (it.length <= 1 && it.all { char -> char.isDigit() }) {
                        rating = it
                    }
                },
                label = { Text("Rating (1–5)") },
                placeholder = { Text("Masukkan angka 1–5") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = ulasan,
                onValueChange = { ulasan = it },
                label = { Text("Ulasan Pengalaman") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = fieldColors
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (pesanError.isNotEmpty()) {
            Text(
                text = pesanError,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                val error = when {
                    namaTempat.isBlank() ->
                        "Nama tempat wajib diisi!"

                    lokasi.isBlank() ->
                        "Lokasi wajib diisi!"

                    status == "Sudah Dikunjungi" && tanggal.isBlank() ->
                        "Tanggal kunjungan wajib diisi!"

                    status == "Sudah Dikunjungi" &&
                            (rating.toIntOrNull() !in 1..5) ->
                        "Rating harus berupa angka 1 sampai 5!"

                    else -> ""
                }

                pesanError = error

                if (error.isEmpty()) {
                    onSimpan(
                        namaTempat.trim(),
                        kategori,
                        lokasi.trim(),
                        status,
                        if (status == "Sudah Dikunjungi") tanggal.trim() else "",
                        if (status == "Sudah Dikunjungi") rating else "",
                        if (status == "Sudah Dikunjungi") ulasan.trim() else "",
                        gambarUri
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = pink
            )
        ) {
            Text(
                if (dataAwal == null) "Simpan Destinasi" else "Simpan Perubahan",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onKembali,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kembali", color = darkBrown)
        }
    }

    if (tampilkanKalender) {
        DatePickerDialog(
            onDismissRequest = { tampilkanKalender = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            tanggal = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                                .format(
                                    DateTimeFormatter.ofPattern(
                                        "dd MMMM yyyy",
                                        Locale("id", "ID")
                                    )
                                )
                        }
                        tampilkanKalender = false
                    }
                ) {
                    Text("Pilih", color = darkBrown)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { tampilkanKalender = false }
                ) {
                    Text("Batal", color = darkBrown)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
