package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.FamilyFileEntity
import com.example.data.model.FileCategory
import com.example.ui.FamilyViewModel

@Composable
fun CloudMediaScreen(
    files: List<FamilyFileEntity>,
    viewModel: FamilyViewModel,
    modifier: Modifier = Modifier
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = FileCategory.entries
    val showUploadSheet by viewModel.showUploadSheet.collectAsState()

    val filteredFiles = remember(selectedCategoryIndex, files) {
        val cat = categories[selectedCategoryIndex]
        if (cat == FileCategory.ALL) files
        else files.filter { it.category == cat }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("cloud_media_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Private Family Cloud",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Secure vault for documents, memories & certificates",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Button(
                            onClick = { viewModel.setShowUploadSheet(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("upload_file_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Upload", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Storage Quota Enforcement Card
            StorageQuotaCard()

            // Filter Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedCategoryIndex,
                edgePadding = 16.dp,
                containerColor = Color.White,
                contentColor = Color(0xFF1E3A8A)
            ) {
                categories.forEachIndexed { index, cat ->
                    Tab(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        text = {
                            Text(
                                text = cat.label,
                                fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            // File Items List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredFiles, key = { it.id }) { file ->
                    FamilyFileItem(file = file)
                }
            }
        }

        if (showUploadSheet) {
            UploadFileDialog(
                onDismiss = { viewModel.setShowUploadSheet(false) },
                onUpload = { name, cat, size ->
                    viewModel.uploadFile(name, cat, size)
                }
            )
        }
    }
}

@Composable
private fun StorageQuotaCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Unlimited Family Cloud",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Unlimited offer sources • No limitation for family",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = "NO LIMITS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF166534),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Family Storage: 18.4 GB",
                        fontSize = 12.sp,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Unlimited Tier • 100% Free",
                        fontSize = 11.sp,
                        color = Color(0xFF059669),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Storage breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StorageTag(label = "Photos", amount = "9.2 GB", color = Color(0xFFD97706))
                StorageTag(label = "Videos", amount = "6.5 GB", color = Color(0xFF7C3AED))
                StorageTag(label = "Docs", amount = "2.7 GB", color = Color(0xFF059669))
            }
        }
    }
}

@Composable
private fun StorageTag(label: String, amount: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$label: $amount",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
private fun FamilyFileItem(file: FamilyFileEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (file.category) {
                            FileCategory.DOCUMENT -> Color(0xFFEFF6FF)
                            FileCategory.VIDEO -> Color(0xFFF3E8FF)
                            FileCategory.PHOTO -> Color(0xFFFEF3C7)
                            FileCategory.ALL -> Color(0xFFF1F5F9)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (file.category) {
                        FileCategory.DOCUMENT -> Icons.Default.Description
                        FileCategory.VIDEO -> Icons.Default.Movie
                        FileCategory.PHOTO -> Icons.Default.Photo
                        FileCategory.ALL -> Icons.Default.Folder
                    },
                    contentDescription = null,
                    tint = when (file.category) {
                        FileCategory.DOCUMENT -> Color(0xFF1E3A8A)
                        FileCategory.VIDEO -> Color(0xFF7C3AED)
                        FileCategory.PHOTO -> Color(0xFFD97706)
                        FileCategory.ALL -> Color(0xFF475569)
                    },
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.fileName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A),
                    maxLines = 1
                )
                Text(
                    text = "${file.sizeText} • Uploaded by ${file.uploaderName} on ${file.uploadedDate}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            IconButton(onClick = { /* Simulate download/open */ }) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download File",
                    tint = Color(0xFF1E3A8A),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun UploadFileDialog(
    onDismiss: () -> Unit,
    onUpload: (name: String, category: FileCategory, sizeText: String) -> Unit
) {
    var fileName by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf(FileCategory.DOCUMENT) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Upload to Family Cloud",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Stored encrypted and counted towards family quota",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name (e.g. Passport_Copy.pdf)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Category", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(FileCategory.DOCUMENT, FileCategory.PHOTO, FileCategory.VIDEO).forEach { cat ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedCat == cat) Color(0xFF1E3A8A) else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedCat = cat }
                        ) {
                            Text(
                                text = cat.label,
                                color = if (selectedCat == cat) Color.White else Color(0xFF334155),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = Color(0xFF475569))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (fileName.isNotBlank()) {
                                onUpload(fileName.trim(), selectedCat, "3.4 MB")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                        shape = RoundedCornerShape(10.dp),
                        enabled = fileName.isNotBlank()
                    ) {
                        Text("Save File", color = Color.White)
                    }
                }
            }
        }
    }
}
