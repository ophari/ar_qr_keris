package com.example.ui.ar

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.graphics3d.Interactive3DViewer
import com.example.graphics3d.PusakaMeshFactory
import com.example.graphics3d.RenderMode
import com.example.qr.KerisQrCatalog
import com.example.qr.KerisQrTarget
import com.example.qr.QrCameraAnalyzer
import com.example.qr.SimpleQrGenerator
import java.util.concurrent.Executors

@Composable
fun SimpleKerisArScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isSimulationMode by remember { mutableStateOf(!hasCameraPermission) }
    var detectedKeris by remember { mutableStateOf<KerisQrTarget?>(null) }
    var isScanning by remember { mutableStateOf(true) }
    var showQrCardDialog by remember { mutableStateOf(false) }
    var previewQrTarget by remember { mutableStateOf(KerisQrCatalog.targets[0]) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            isSimulationMode = false
        }
    }

    // Scanning line animation in viewfinder
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_anim")
    val scanLineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan_line"
    )

    // Pulse animation when Keris appears
    val reticleScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "reticle_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0706))
    ) {
        // 1. Camera View / Simulated Camera Backdrop
        if (hasCameraPermission && !isSimulationMode) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraExecutor = Executors.newSingleThreadExecutor()
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(
                                    cameraExecutor,
                                    QrCameraAnalyzer { detectedCode ->
                                        if (isScanning && detectedKeris == null) {
                                            val target = KerisQrCatalog.findTarget(detectedCode)
                                            if (target != null) {
                                                detectedKeris = target
                                                isScanning = false
                                            }
                                        }
                                    }
                                )
                            }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (_: Exception) {
                            isSimulationMode = true
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Simulated AR Camera Floor / Surface
            SimulatedArEnvironment()
        }

        // 2. Camera Viewfinder Overlay (When Scanning)
        if (detectedKeris == null) {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0x9900E5FF), RoundedCornerShape(16.dp))
            ) {
                // Scanning Laser Line
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val lineY = size.height * scanLineProgress
                    drawLine(
                        color = Color(0xFF00E5FF),
                        start = Offset(0f, lineY),
                        end = Offset(size.width, lineY),
                        strokeWidth = 3f
                    )
                    // Glow under laser line
                    drawLine(
                        color = Color(0x5500E5FF),
                        start = Offset(0f, lineY - 6f),
                        end = Offset(size.width, lineY - 6f),
                        strokeWidth = 10f
                    )
                }

                // Four Corner Accents
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(24.dp)
                        .border(4.dp, Color(0xFFFFD54F), RoundedCornerShape(topStart = 8.dp))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .border(4.dp, Color(0xFFFFD54F), RoundedCornerShape(topEnd = 8.dp))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .size(24.dp)
                        .border(4.dp, Color(0xFFFFD54F), RoundedCornerShape(bottomStart = 8.dp))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .border(4.dp, Color(0xFFFFD54F), RoundedCornerShape(bottomEnd = 8.dp))
                )
            }
        }

        // 3. 3D Keris Projection in AR Space (When QR Code Scanned / Unlocked)
        detectedKeris?.let { keris ->
            val kerisMesh = remember(keris.lukCount) {
                PusakaMeshFactory.createKerisMesh(keris.lukCount, keris.pusakaName)
            }

            Interactive3DViewer(
                mesh = kerisMesh,
                renderMode = RenderMode.REALISTIC_PAMOR,
                isAutoRotate = true,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp)
                    .scale(reticleScale)
            )
        }

        // 4. Top Telemetry & QR Helper Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Pill
                Surface(
                    color = Color(0xD910172A),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (detectedKeris != null) Color(0xFF00E676) else Color(0x6600E5FF)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .background(
                                    if (detectedKeris != null) Color(0xFF00E676) else Color(0xFF00E5FF),
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (detectedKeris != null) "Keris 3D Muncul!" else "Arahkan ke QR Keris",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Action Buttons: Sample QR Modal & Camera Permission/Mode
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Button to display printable/scannable QR cards
                    Button(
                        onClick = { showQrCardDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.testTag("show_sample_qr_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QR Marker",
                            tint = Color(0xFF261803),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Contoh QR",
                            color = Color(0xFF261803),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Camera Switch
                    IconButton(
                        onClick = {
                            if (!hasCameraPermission) {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            } else {
                                isSimulationMode = !isSimulationMode
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0x991E293B), CircleShape)
                            .testTag("camera_permission_toggle")
                    ) {
                        Icon(
                            imageVector = if (isSimulationMode) Icons.Default.VideocamOff else Icons.Default.Videocam,
                            contentDescription = "Mode Kamera",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Instruction Banner
            Surface(
                color = Color(0xCC000000),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (detectedKeris == null) {
                        "Arahkan kamera ke QR Code Keris, atau ketuk tombol 'Simulasi Scan QR' di bawah untuk langsung memunculkan 3D Keris."
                    } else {
                        "Model 3D Keris berhasil dimunculkan di bidang kamera! Sentuh dan geser untuk memutar model 3D 360°."
                    },
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        // 5. Bottom Control Sheet
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xEE0B0907), Color(0xFF0C0908))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            AnimatedVisibility(
                visible = detectedKeris != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                detectedKeris?.let { keris ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1511)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD4AF37)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = keris.pusakaName,
                                        color = Color(0xFFFFD54F),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${keris.origin} • Bilah Luk ${keris.lukCount}",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                }
                                Surface(
                                    color = Color(0x3300E676),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "QR TERDETEKSI",
                                        color = Color(0xFF00E676),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = keris.description,
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Quick Scan Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (detectedKeris == null) {
                    // One-click Simulate QR Scan (essential for emulator & instant testing)
                    Button(
                        onClick = {
                            detectedKeris = KerisQrCatalog.targets[0] // Default Luk 9
                            isScanning = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("simulate_scan_qr_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color(0xFF051726),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simulasi Scan QR Keris",
                            color = Color(0xFF051726),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    // Reset / Scan Again Button
                    Button(
                        onClick = {
                            detectedKeris = null
                            isScanning = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E221B)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("scan_again_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Scan QR Lain",
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Cycle Luk variations (Luk 3, 5, 7, 9, 13)
                    Button(
                        onClick = {
                            val currentLuk = detectedKeris?.lukCount ?: 9
                            val nextTarget = when (currentLuk) {
                                3 -> KerisQrCatalog.targets[1] // Luk 13
                                13 -> KerisQrCatalog.targets[2] // Luk 5
                                5 -> KerisQrCatalog.targets[3] // Luk 7
                                7 -> KerisQrCatalog.targets[0] // Luk 9
                                else -> KerisQrCatalog.targets[4] // Luk 3
                            }
                            detectedKeris = nextTarget
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("switch_luk_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF261803),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ganti Luk",
                            color = Color(0xFF261803),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // Modal Dialog showing Sample QR Codes to Scan or Test
    if (showQrCardDialog) {
        Dialog(onDismissRequest = { showQrCardDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1714)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD4AF37)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kartu QR Marker Keris",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        IconButton(
                            onClick = { showQrCardDialog = false },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Scan QR ini dengan kamera HP lain atau ketuk 'Gunakan Marker Ini' untuk langsung mengaktifkan 3D Keris.",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // QR Selector Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        KerisQrCatalog.targets.forEach { target ->
                            val isSelected = target.qrCode == previewQrTarget.qrCode
                            Surface(
                                color = if (isSelected) Color(0xFFD4AF37) else Color(0xFF2E221B),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable { previewQrTarget = target }
                            ) {
                                Text(
                                    text = "Luk ${target.lukCount}",
                                    color = if (isSelected) Color(0xFF261803) else Color(0xFFE2E8F0),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rendered Visual QR Code
                    val qrBitmap = remember(previewQrTarget.qrCode) {
                        SimpleQrGenerator.generateQrBitmap(previewQrTarget.qrCode, size = 260)
                    }

                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = qrBitmap,
                            contentDescription = "QR Code ${previewQrTarget.pusakaName}",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = previewQrTarget.pusakaName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                    Text(
                        text = "Kode QR: ${previewQrTarget.qrCode}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            detectedKeris = previewQrTarget
                            isScanning = false
                            showQrCardDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("apply_marker_button")
                    ) {
                        Text(
                            text = "Gunakan Marker Ini Sekarang",
                            color = Color(0xFF051726),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Simulated Studio Environment when physical camera is inactive.
 */
@Composable
private fun SimulatedArEnvironment() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF1E293B), Color(0xFF0B0F19), Color(0xFF030712))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val horizonY = size.height * 0.50f
            val groundHeight = size.height - horizonY

            // Perspective grid lines
            for (i in 1..10) {
                val factor = (i / 10f) * (i / 10f)
                val lineY = horizonY + factor * groundHeight
                drawLine(
                    color = Color(0x2200E5FF),
                    start = Offset(0f, lineY),
                    end = Offset(size.width, lineY),
                    strokeWidth = 1.0f
                )
            }

            val vanishingPointX = size.width / 2f
            for (x in -3..13) {
                val bottomX = x * (size.width / 10f)
                drawLine(
                    color = Color(0x1800E5FF),
                    start = Offset(vanishingPointX, horizonY),
                    end = Offset(bottomX, size.height),
                    strokeWidth = 1.0f
                )
            }
        }
    }
}
