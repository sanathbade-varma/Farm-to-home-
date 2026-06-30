package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FarmToHomeMainScreen(
    viewModel: FarmToHomeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeOrder = viewModel.activeOrder

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Farm-to-Home Hub",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    val gmmIntentUri = Uri.parse("geo:0,0?q=Sacramento, CA")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (e: Exception) {
                                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=Sacramento, CA"))
                                        context.startActivity(browserIntent)
                                    }
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sacramento, CA",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.currentTab = 2 },
                        modifier = Modifier.testTag("action_copilot_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "AI Copilot",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = viewModel.currentTab == 0,
                    onClick = { viewModel.currentTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Marketplace", fontWeight = FontWeight.Medium) },
                    modifier = Modifier.testTag("nav_marketplace")
                )
                NavigationBarItem(
                    selected = viewModel.currentTab == 1,
                    onClick = { viewModel.currentTab = 1 },
                    icon = { Icon(Icons.Default.AccountBox, contentDescription = "Portals") },
                    label = { Text("Portals", fontWeight = FontWeight.Medium) },
                    modifier = Modifier.testTag("nav_portals")
                )
                NavigationBarItem(
                    selected = viewModel.currentTab == 2,
                    onClick = { viewModel.currentTab = 2 },
                    icon = { Icon(Icons.Default.Face, contentDescription = "Copilot") },
                    label = { Text("AI Copilot", fontWeight = FontWeight.Medium) },
                    modifier = Modifier.testTag("nav_copilot")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live notification bar for direct booking / active delivery updates
            AnimatedVisibility(
                visible = activeOrder != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                activeOrder?.let { order ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Delivery Info",
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Order History Tracker",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Item: ${order.productName}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        text = "Status: ${order.status}",
                                        fontSize = 12.sp
                                    )
                                }
                                IconButton(onClick = { viewModel.clearActiveDelivery() }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Close tracker",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Dynamic Visual Progress Bar
                            val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
                                targetValue = order.progress,
                                animationSpec = androidx.compose.animation.core.tween(durationMillis = 800)
                            )
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f),
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${(order.progress * 100).toInt()}% Completed",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "ETA: ${order.eta}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (viewModel.currentTab) {
                    0 -> MarketplaceScreen(viewModel, context)
                    1 -> PortalsScreen(viewModel, context)
                    2 -> CopilotScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun MarketplaceScreen(viewModel: FarmToHomeViewModel, context: Context) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Hero Image Display with beautiful round corners and glassmorphism trust card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                // Try resolving img_farm_to_home_1782635859004 fallback to ic_launcher if any issue
                val imagePainter = painterResource(id = R.drawable.img_farm_to_home_1782635859004)
                Image(
                    painter = imagePainter,
                    contentDescription = "Lush organic farm with farmer directly connecting to digital marketplace",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Overlap badge for branding
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .background(
                            Color(0xCD1B2E1C),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "100% Direct From Farmers",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        item {
            // Trust & Core Pillars section
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Connecting Farmers Directly to Your Doorstep",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "By bypassing traditional middlemen, we secure fair pay for farmers, 100% compostable packaging, and eco-friendly EV deliveries direct to clients.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PillarBadge(Icons.Default.Check, "Fair Pricing")
                        PillarBadge(Icons.Default.Info, "Eco-Packaged")
                        PillarBadge(Icons.Default.LocationOn, "Local Farms")
                    }
                }
            }
        }

        item {
            Text(
                text = "Fresh Organic Harvest",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Fresh organic products list
        items(viewModel.products) { product ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("product_card_${product.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                var isExpanded by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded }
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Category-specific placeholder icons for fresh farm products
                        val icon = when (product.category) {
                            "Fruits" -> Icons.Default.Home
                            "Vegetables" -> Icons.Default.Check
                            else -> Icons.Default.Info
                        }

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = product.category,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = product.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.secondaryContainer,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = product.stockStatus,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                            Text(
                                text = "Grown with trust by: ${product.farmerName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${product.price} per ${product.unit}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = "Expand details",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.orderProduct(product) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("buy_product_${product.id}"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Buy")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Order Direct & Track Delivery", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PillarBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PortalsScreen(viewModel: FarmToHomeViewModel, context: Context) {
    var expandedSection by remember { mutableStateOf(0) } // 0: Client, 1: Farmer, 2: Agent, 3: Seller

    val clientList by viewModel.clientAddresses.collectAsStateWithLifecycle()
    val farmerList by viewModel.farmerRegistrations.collectAsStateWithLifecycle()
    val agentList by viewModel.deliveryAgentRegistrations.collectAsStateWithLifecycle()
    val sellerList by viewModel.sellerAddresses.collectAsStateWithLifecycle()

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Marketplace Registration Portals",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Activate and manage your ecosystem profiles securely in real-time. Use coordinates & location access for automated delivery assignment.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // --- 1. Client Address Registration ---
        item {
            PortalSectionHeader(
                title = "Client Address Registration",
                isExpanded = expandedSection == 0,
                onHeaderClick = { expandedSection = 0 },
                badgeCount = clientList.size
            )

            AnimatedVisibility(visible = expandedSection == 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = viewModel.clientFormName,
                            onValueChange = { viewModel.clientFormName = it },
                            label = { Text("Client Full Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("client_name_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = viewModel.clientFormPhone,
                            onValueChange = { viewModel.clientFormPhone = it },
                            label = { Text("Contact Phone") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("client_phone_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = viewModel.clientFormAddress,
                            onValueChange = { viewModel.clientFormAddress = it },
                            label = { Text("Delivery Street Address") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("client_address_input"),
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Location access flow
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (locationPermissionState.status.isGranted) {
                                Button(
                                    onClick = { viewModel.fetchGPSCoordinates(context, forSeller = false) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = "GPS")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Retrieve GPS", fontSize = 12.sp)
                                }
                            } else {
                                Button(
                                    onClick = { locationPermissionState.launchPermissionRequest() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = "Request Location")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Grant Location Access", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Card(
                                modifier = Modifier.weight(1.1f),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Telemetry Coordinates", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "Lat: ${viewModel.clientFormLat}\nLng: ${viewModel.clientFormLng}",
                                        fontSize = 10.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        if (viewModel.isFetchingLocation) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
                        }

                        if (viewModel.clientFormStatus.isNotEmpty()) {
                            Text(
                                text = viewModel.clientFormStatus,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.submitClientAddress() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("client_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Register Address Location", fontWeight = FontWeight.Bold)
                        }

                        // Local records
                        if (clientList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Registered Client Addresses:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            clientList.forEach { client ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1.1f)) {
                                        Text("${client.name} (${client.phone})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(client.address, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Coord: [${client.latitude}, ${client.longitude}]", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                                    }
                                    IconButton(onClick = { viewModel.deleteClientAddress(client.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Farmer Registration Portal ---
        item {
            PortalSectionHeader(
                title = "Farmer Registration Portal",
                isExpanded = expandedSection == 1,
                onHeaderClick = { expandedSection = 1 },
                badgeCount = farmerList.size
            )

            AnimatedVisibility(visible = expandedSection == 1) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = viewModel.farmerFormName,
                            onValueChange = { viewModel.farmerFormName = it },
                            label = { Text("Farmer Full Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("farmer_name_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = viewModel.farmerFormFarmName,
                            onValueChange = { viewModel.farmerFormFarmName = it },
                            label = { Text("Farm Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("farmer_farm_name_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = viewModel.farmerFormLocation,
                            onValueChange = { viewModel.farmerFormLocation = it },
                            label = { Text("Farm Geographical Region") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("farmer_location_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = viewModel.farmerFormProduce,
                            onValueChange = { viewModel.farmerFormProduce = it },
                            label = { Text("Primary Crops (Fruits/Vegetables/Grains)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("farmer_produce_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = viewModel.farmerFormCertified,
                                onCheckedChange = { viewModel.farmerFormCertified = it }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Has Active Sustainable Organic Certification", fontSize = 12.sp)
                        }

                        if (viewModel.farmerFormStatus.isNotEmpty()) {
                            Text(
                                text = viewModel.farmerFormStatus,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.submitFarmerRegistration() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("farmer_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Activate Farmer Portal Profile", fontWeight = FontWeight.Bold)
                        }

                        // Local records
                        if (farmerList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Registered Farmer Profiles:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            farmerList.forEach { farmer ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1.1f)) {
                                        Text("${farmer.farmerName} - ${farmer.farmName}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Region: ${farmer.farmLocation} | Produce: ${farmer.majorProduce}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(if (farmer.hasCertificate) "✓ Certified Organic" else "Pending Certification", fontSize = 10.sp, color = if (farmer.hasCertificate) Color(0xFF2E7D32) else Color.Gray)
                                    }
                                    IconButton(onClick = { viewModel.deleteFarmerRegistration(farmer.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 3. Delivery Agent Registration Portal ---
        item {
            PortalSectionHeader(
                title = "Delivery Agent Portal",
                isExpanded = expandedSection == 2,
                onHeaderClick = { expandedSection = 2 },
                badgeCount = agentList.size
            )

            AnimatedVisibility(visible = expandedSection == 2) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = viewModel.agentFormName,
                            onValueChange = { viewModel.agentFormName = it },
                            label = { Text("Agent Driver Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("agent_name_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = viewModel.agentFormLicense,
                            onValueChange = { viewModel.agentFormLicense = it },
                            label = { Text("Driving License Details") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("agent_license_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Select Eco-Friendly Vehicle:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        val vehicles = listOf("Electric Cargo Van", "Electric Cargo Bicycle", "Solar Hybrid Truck", "Standard Scooter")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            vehicles.forEach { vehicle ->
                                val selected = viewModel.agentFormVehicleType == vehicle
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.agentFormVehicleType = vehicle }
                                        .padding(horizontal = 4.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = vehicle,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        if (viewModel.agentFormStatus.isNotEmpty()) {
                            Text(
                                text = viewModel.agentFormStatus,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.submitDeliveryAgentRegistration() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("agent_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Register Active Agent Route", fontWeight = FontWeight.Bold)
                        }

                        // Local records
                        if (agentList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Registered Delivery Agents:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            agentList.forEach { agent ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1.1f)) {
                                        Text(agent.agentName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("License: ${agent.licenseNumber} | Vehicle: ${agent.vehicleType}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Hub Assign: ${agent.activeHub}", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                                    }
                                    IconButton(onClick = { viewModel.deleteDeliveryAgentRegistration(agent.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 4. Seller Address Registration ---
        item {
            PortalSectionHeader(
                title = "Seller Coordinates Portal",
                isExpanded = expandedSection == 3,
                onHeaderClick = { expandedSection = 3 },
                badgeCount = sellerList.size
            )

            AnimatedVisibility(visible = expandedSection == 3) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = viewModel.sellerFormBusinessName,
                            onValueChange = { viewModel.sellerFormBusinessName = it },
                            label = { Text("Business Entity Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("seller_name_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = viewModel.sellerFormWarehouseAddress,
                            onValueChange = { viewModel.sellerFormWarehouseAddress = it },
                            label = { Text("Warehouse/Storage Base Address") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("seller_address_input"),
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = viewModel.sellerFormTaxId,
                            onValueChange = { viewModel.sellerFormTaxId = it },
                            label = { Text("Corporate Tax ID (GSTIN/EIN)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("seller_tax_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = viewModel.sellerFormContact,
                            onValueChange = { viewModel.sellerFormContact = it },
                            label = { Text("Support HotLine Contact") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("seller_contact_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Retrieve GPS Coordinates specifically for seller Warehouse
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (locationPermissionState.status.isGranted) {
                                Button(
                                    onClick = { viewModel.fetchGPSCoordinates(context, forSeller = true) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = "Warehouse GPS")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("GPS Warehouse", fontSize = 11.sp)
                                }
                            } else {
                                Button(
                                    onClick = { locationPermissionState.launchPermissionRequest() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = "Permission")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Grant GPS", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Card(
                                modifier = Modifier.weight(1.1f),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Warehouse Lat/Lng", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "Lat: ${viewModel.sellerFormLat}\nLng: ${viewModel.sellerFormLng}",
                                        fontSize = 10.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        if (viewModel.sellerFormStatus.isNotEmpty()) {
                            Text(
                                text = viewModel.sellerFormStatus,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.submitSellerAddress() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("seller_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Register Warehouse Coordinates", fontWeight = FontWeight.Bold)
                        }

                        // Local records
                        if (sellerList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Registered Hub Warehouses:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            sellerList.forEach { seller ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1.1f)) {
                                        Text(seller.businessName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Warehouse: ${seller.warehouseAddress}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Tax ID: ${seller.taxId} | Phone: ${seller.contactPhone}", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                                    }
                                    IconButton(onClick = { viewModel.deleteSellerAddress(seller.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PortalSectionHeader(
    title: String,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    badgeCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHeaderClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = if (isExpanded) RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp) else RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Check else Icons.Default.Add,
                contentDescription = "Expand Status",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CopilotScreen(viewModel: FarmToHomeViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Copilot Introduction Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "AgriCopilot Avatar",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AgriCopilot • Live Route & Crop Advisor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Optimized routing telemetry, fair prices & sustainability calculator.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chat list
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .weight(1.1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = "Idle Copilot",
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Start an organic agriculture & pricing advisory chat!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }
            } else {
                items(messages) { message ->
                    val isBot = !message.isUser
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isBot) Arrangement.Start else Arrangement.End
                    ) {
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (isBot) 2.dp else 12.dp,
                                bottomEnd = if (isBot) 12.dp else 2.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isBot) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer,
                                contentColor = if (isBot) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = message.message,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isBot) "AgriCopilot" else "You",
                                    fontSize = 8.sp,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }

            if (viewModel.isCopilotLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AgriCopilot is analyzing details...", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        // Suggestions
        Spacer(modifier = Modifier.height(8.dp))
        Text("Quick-Suggestions:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val suggestions = listOf(
                "Optimize tomato EV delivery route" to "How do EV delivery routes optimize shelf-life for organic tomatoes?",
                "Organic compost suggestions" to "What standard organic compost ingredients are best for local grain fields?",
                "Calculate fair wheat price" to "What is a fair pricing formula for organic stone-ground wheat per bag?"
            )
            items(suggestions) { (title, prompt) ->
                Card(
                    modifier = Modifier.clickable { viewModel.triggerQuickPrompt(prompt) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.clearChat() },
                modifier = Modifier.testTag("chat_clear_button")
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Clear History", tint = Color.Red)
            }

            Spacer(modifier = Modifier.width(4.dp))

            OutlinedTextField(
                value = viewModel.copilotInput,
                onValueChange = { viewModel.copilotInput = it },
                placeholder = { Text("Ask AgriCopilot about route optimization...") },
                modifier = Modifier
                    .weight(1.1f)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                singleLine = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            FloatingActionButton(
                onClick = { viewModel.sendCopilotMessage() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send prompt",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
