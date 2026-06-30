package com.example.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.*
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FarmToHomeViewModel(private val repository: MainRepository) : ViewModel() {

    // --- Tab state ---
    var currentTab by mutableStateOf(0) // 0: Home/Marketplace, 1: Portals/Registration, 2: AI Copilot

    // --- Marketplace Products State ---
    val products = listOf(
        FarmProduct(1, "Organic Honeycrisp Apples", "Fruits", "$4.50", "kg", R.drawable.ic_launcher_foreground, "In Stock", "Crisp, sweet, and hand-picked at peak ripeness from GreenHill Orchards.", "Farmer Samuel"),
        FarmProduct(2, "Heirloom Beefsteak Tomatoes", "Vegetables", "$3.20", "kg", R.drawable.ic_launcher_foreground, "In Stock", "Juicy, rich, sun-ripened tomatoes grown with compost-only fertilizers.", "Farmer Beatrice"),
        FarmProduct(3, "Stone-Ground Whole Grain Wheat", "Grains", "$6.00", "bag", R.drawable.ic_launcher_foreground, "In Stock", "Freshly milled stone-ground premium grains, perfect for artisanal breads.", "Farmer Clara"),
        FarmProduct(4, "Sweet Baby Corn (Organic)", "Vegetables", "$2.80", "bunch", R.drawable.ic_launcher_foreground, "In Stock", "Tender, naturally sweet organic corn packed in compostable fiber bags.", "Farmer Beatrice"),
        FarmProduct(5, "Fresh Hass Avocados", "Fruits", "$5.50", "box", R.drawable.ic_launcher_foreground, "Low Stock", "Buttery, high-density heart-healthy avocados with zero-carbon logistics.", "Farmer Samuel"),
        FarmProduct(6, "Fluffy Organic Brown Rice", "Grains", "$4.00", "kg", R.drawable.ic_launcher_foreground, "In Stock", "Traditional high-fiber brown rice grown using artisanal water-conserving techniques.", "Farmer Clara")
    )

    // --- Active Deliveries Status ---
    var activeOrder by mutableStateOf<ActiveOrder?>(null)

    // Job for active delivery simulation
    private var deliveryJob: kotlinx.coroutines.Job? = null

    // --- Database Flows ---
    val clientAddresses: StateFlow<List<ClientAddressEntity>> = repository.clientAddresses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val farmerRegistrations: StateFlow<List<FarmerRegistrationEntity>> = repository.farmerRegistrations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deliveryAgentRegistrations: StateFlow<List<DeliveryAgentRegistrationEntity>> = repository.deliveryAgentRegistrations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sellerAddresses: StateFlow<List<SellerAddressEntity>> = repository.sellerAddresses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Form Input States ---
    // Client Address
    var clientFormName by mutableStateOf("")
    var clientFormPhone by mutableStateOf("")
    var clientFormAddress by mutableStateOf("")
    var clientFormLat by mutableStateOf(0.0)
    var clientFormLng by mutableStateOf(0.0)
    var clientFormStatus by mutableStateOf("")

    // Farmer Registration
    var farmerFormName by mutableStateOf("")
    var farmerFormFarmName by mutableStateOf("")
    var farmerFormLocation by mutableStateOf("")
    var farmerFormProduce by mutableStateOf("")
    var farmerFormCertified by mutableStateOf(true)
    var farmerFormStatus by mutableStateOf("")

    // Delivery Agent Registration
    var agentFormName by mutableStateOf("")
    var agentFormVehicleType by mutableStateOf("Electric Cargo Van")
    var agentFormLicense by mutableStateOf("")
    var agentFormHub by mutableStateOf("Greenfield Central Eco-Hub")
    var agentFormStatus by mutableStateOf("")

    // Seller Address Registration
    var sellerFormBusinessName by mutableStateOf("")
    var sellerFormWarehouseAddress by mutableStateOf("")
    var sellerFormTaxId by mutableStateOf("")
    var sellerFormContact by mutableStateOf("")
    var sellerFormLat by mutableStateOf(0.0)
    var sellerFormLng by mutableStateOf(0.0)
    var sellerFormStatus by mutableStateOf("")

    // Location loading feedback
    var isFetchingLocation by mutableStateOf(false)

    // --- AI Copilot Chat States ---
    var copilotInput by mutableStateOf("")
    var isCopilotLoading by mutableStateOf(false)

    // --- Form Submissions ---
    fun submitClientAddress() {
        if (clientFormName.isBlank() || clientFormPhone.isBlank() || clientFormAddress.isBlank()) {
            clientFormStatus = "Please fill in all client address fields."
            return
        }
        viewModelScope.launch {
            repository.insertClientAddress(
                ClientAddressEntity(
                    name = clientFormName,
                    phone = clientFormPhone,
                    address = clientFormAddress,
                    latitude = if (clientFormLat == 0.0) 37.7749 else clientFormLat,
                    longitude = if (clientFormLng == 0.0) -122.4194 else clientFormLng
                )
            )
            clientFormStatus = "Client delivery address registered successfully!"
            // Reset fields
            clientFormName = ""
            clientFormPhone = ""
            clientFormAddress = ""
            clientFormLat = 0.0
            clientFormLng = 0.0
        }
    }

    fun deleteClientAddress(id: Long) {
        viewModelScope.launch {
            repository.deleteClientAddress(id)
        }
    }

    fun submitFarmerRegistration() {
        if (farmerFormName.isBlank() || farmerFormFarmName.isBlank() || farmerFormLocation.isBlank()) {
            farmerFormStatus = "Please fill in all farmer registration fields."
            return
        }
        viewModelScope.launch {
            repository.insertFarmerRegistration(
                FarmerRegistrationEntity(
                    farmerName = farmerFormName,
                    farmName = farmerFormFarmName,
                    farmLocation = farmerFormLocation,
                    majorProduce = farmerFormProduce.ifBlank { "Mixed Organic Vegetables" },
                    hasCertificate = farmerFormCertified
                )
            )
            farmerFormStatus = "Farmer portal profile activated!"
            farmerFormName = ""
            farmerFormFarmName = ""
            farmerFormLocation = ""
            farmerFormProduce = ""
            farmerFormCertified = true
        }
    }

    fun deleteFarmerRegistration(id: Long) {
        viewModelScope.launch {
            repository.deleteFarmerRegistration(id)
        }
    }

    fun submitDeliveryAgentRegistration() {
        if (agentFormName.isBlank() || agentFormLicense.isBlank()) {
            agentFormStatus = "Please enter delivery agent name and driving license details."
            return
        }
        viewModelScope.launch {
            repository.insertDeliveryAgentRegistration(
                DeliveryAgentRegistrationEntity(
                    agentName = agentFormName,
                    vehicleType = agentFormVehicleType,
                    licenseNumber = agentFormLicense,
                    activeHub = agentFormHub
                )
            )
            agentFormStatus = "Eco-Delivery agent registered & route-assigned!"
            agentFormName = ""
            agentFormLicense = ""
            agentFormVehicleType = "Electric Cargo Van"
        }
    }

    fun deleteDeliveryAgentRegistration(id: Long) {
        viewModelScope.launch {
            repository.deleteDeliveryAgentRegistration(id)
        }
    }

    fun submitSellerAddress() {
        if (sellerFormBusinessName.isBlank() || sellerFormWarehouseAddress.isBlank() || sellerFormTaxId.isBlank()) {
            sellerFormStatus = "Please fill in all business registration fields."
            return
        }
        viewModelScope.launch {
            repository.insertSellerAddress(
                SellerAddressEntity(
                    businessName = sellerFormBusinessName,
                    warehouseAddress = sellerFormWarehouseAddress,
                    taxId = sellerFormTaxId,
                    contactPhone = sellerFormContact
                )
            )
            sellerFormStatus = "Seller hub business coordinates registered!"
            sellerFormBusinessName = ""
            sellerFormWarehouseAddress = ""
            sellerFormTaxId = ""
            sellerFormContact = ""
            sellerFormLat = 0.0
            sellerFormLng = 0.0
        }
    }

    fun deleteSellerAddress(id: Long) {
        viewModelScope.launch {
            repository.deleteSellerAddress(id)
        }
    }

    // --- Google Location Access integration ---
    @SuppressLint("MissingPermission")
    fun fetchGPSCoordinates(context: Context, forSeller: Boolean = false) {
        isFetchingLocation = true
        val locationClient = LocationServices.getFusedLocationProviderClient(context)
        try {
            locationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    isFetchingLocation = false
                    if (location != null) {
                        val lat = Math.round(location.latitude * 10000.0) / 10000.0
                        val lng = Math.round(location.longitude * 10000.0) / 10000.0
                        if (forSeller) {
                            sellerFormLat = lat
                            sellerFormLng = lng
                            sellerFormStatus = "GPS Coordinates retrieved: Lat $lat, Lng $lng"
                        } else {
                            clientFormLat = lat
                            clientFormLng = lng
                            clientFormStatus = "GPS Coordinates retrieved: Lat $lat, Lng $lng"
                        }
                    } else {
                        // Safe fallback coordinates (Greenfield Farm Hub)
                        val lat = 34.0522 + (Math.random() - 0.5) * 0.1
                        val lng = -118.2437 + (Math.random() - 0.5) * 0.1
                        val roundedLat = Math.round(lat * 10000.0) / 10000.0
                        val roundedLng = Math.round(lng * 10000.0) / 10000.0
                        if (forSeller) {
                            sellerFormLat = roundedLat
                            sellerFormLng = roundedLng
                            sellerFormStatus = "Network GPS Timeout. Autopopulated via local Eco-Hub: Lat $roundedLat, Lng $roundedLng"
                        } else {
                            clientFormLat = roundedLat
                            clientFormLng = roundedLng
                            clientFormStatus = "Network GPS Timeout. Autopopulated via local Eco-Hub: Lat $roundedLat, Lng $roundedLng"
                        }
                    }
                }
                .addOnFailureListener { e ->
                    isFetchingLocation = false
                    val lat = 34.0522 + (Math.random() - 0.5) * 0.1
                    val lng = -118.2437 + (Math.random() - 0.5) * 0.1
                    val roundedLat = Math.round(lat * 10000.0) / 10000.0
                    val roundedLng = Math.round(lng * 10000.0) / 10000.0
                    if (forSeller) {
                        sellerFormLat = roundedLat
                        sellerFormLng = roundedLng
                        sellerFormStatus = "GPS Offline. Seeded coordinate reference: Lat $roundedLat, Lng $roundedLng"
                    } else {
                        clientFormLat = roundedLat
                        clientFormLng = roundedLng
                        clientFormStatus = "GPS Offline. Seeded coordinate reference: Lat $roundedLat, Lng $roundedLng"
                    }
                }
        } catch (e: Exception) {
            isFetchingLocation = false
            if (forSeller) {
                sellerFormStatus = "Location services error: ${e.localizedMessage}"
            } else {
                clientFormStatus = "Location services error: ${e.localizedMessage}"
            }
        }
    }

    // --- AI Copilot Messaging ---
    fun sendCopilotMessage() {
        val query = copilotInput.trim()
        if (query.isBlank()) return

        copilotInput = ""
        isCopilotLoading = true
        viewModelScope.launch {
            repository.askAgriCopilot(query)
            isCopilotLoading = false
        }
    }

    fun triggerQuickPrompt(promptText: String) {
        copilotInput = promptText
        sendCopilotMessage()
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChatHistory()
        }
    }

    // --- Buy Product Flow (Triggers active delivery simulator) ---
    fun orderProduct(product: FarmProduct) {
        deliveryJob?.cancel()
        activeOrder = ActiveOrder(
            productName = product.name,
            status = "Booking confirmed! Direct Farm packaging initiated.",
            progress = 0.05f,
            eta = "Calculating..."
        )
        
        deliveryJob = viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            activeOrder = activeOrder?.copy(progress = 0.25f, status = "Farmer packing your organic produce.", eta = "45 mins")
            kotlinx.coroutines.delay(3000)
            activeOrder = activeOrder?.copy(progress = 0.5f, status = "Eco-van picked up the order.", eta = "25 mins")
            kotlinx.coroutines.delay(3000)
            activeOrder = activeOrder?.copy(progress = 0.75f, status = "On the way to your door.", eta = "10 mins")
            kotlinx.coroutines.delay(3000)
            activeOrder = activeOrder?.copy(progress = 1.0f, status = "Delivered safely!", eta = "Arrived")
        }
    }

    fun clearActiveDelivery() {
        deliveryJob?.cancel()
        activeOrder = null
    }
}

data class ActiveOrder(
    val productName: String,
    val status: String,
    val progress: Float,
    val eta: String
)


class FarmToHomeViewModelFactory(private val repository: MainRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FarmToHomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FarmToHomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
