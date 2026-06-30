package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_addresses")
data class ClientAddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "farmer_registrations")
data class FarmerRegistrationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmerName: String,
    val farmName: String,
    val farmLocation: String,
    val majorProduce: String,
    val hasCertificate: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "delivery_agent_registrations")
data class DeliveryAgentRegistrationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val agentName: String,
    val vehicleType: String,
    val licenseNumber: String,
    val activeHub: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "seller_addresses")
data class SellerAddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessName: String,
    val warehouseAddress: String,
    val taxId: String,
    val contactPhone: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val message: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class FarmProduct(
    val id: Int,
    val name: String,
    val category: String,
    val price: String,
    val unit: String,
    val imageResId: Int, // Placeholder local illustration
    val stockStatus: String,
    val description: String,
    val farmerName: String
)
