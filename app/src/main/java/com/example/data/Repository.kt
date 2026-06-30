package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MainRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val clientDao = db.clientAddressDao()
    private val farmerDao = db.farmerRegistrationDao()
    private val deliveryDao = db.deliveryAgentRegistrationDao()
    private val sellerDao = db.sellerAddressDao()
    private val chatDao = db.chatMessageDao()
    private val geminiRepo = GeminiRepository()

    // Client Address
    val clientAddresses: Flow<List<ClientAddressEntity>> = clientDao.getAll()
    suspend fun insertClientAddress(entity: ClientAddressEntity) = clientDao.insert(entity)
    suspend fun deleteClientAddress(id: Long) = clientDao.deleteById(id)

    // Farmer Registration
    val farmerRegistrations: Flow<List<FarmerRegistrationEntity>> = farmerDao.getAll()
    suspend fun insertFarmerRegistration(entity: FarmerRegistrationEntity) = farmerDao.insert(entity)
    suspend fun deleteFarmerRegistration(id: Long) = farmerDao.deleteById(id)

    // Delivery Agent Registration
    val deliveryAgentRegistrations: Flow<List<DeliveryAgentRegistrationEntity>> = deliveryDao.getAll()
    suspend fun insertDeliveryAgentRegistration(entity: DeliveryAgentRegistrationEntity) = deliveryDao.insert(entity)
    suspend fun deleteDeliveryAgentRegistration(id: Long) = deliveryDao.deleteById(id)

    // Seller Address Registration
    val sellerAddresses: Flow<List<SellerAddressEntity>> = sellerDao.getAll()
    suspend fun insertSellerAddress(entity: SellerAddressEntity) = sellerDao.insert(entity)
    suspend fun deleteSellerAddress(id: Long) = sellerDao.deleteById(id)

    // Chat History
    val chatMessages: Flow<List<ChatMessageEntity>> = chatDao.getAll()
    suspend fun insertChatMessage(entity: ChatMessageEntity) = chatDao.insert(entity)
    suspend fun clearChatHistory() = chatDao.deleteAll()

    // Copilot Call
    suspend fun askAgriCopilot(prompt: String): String {
        // Fetch current history
        val history = chatDao.getAll().first()
        // Save user message to DB
        insertChatMessage(ChatMessageEntity(message = prompt, isUser = true))
        
        // Call API
        val reply = geminiRepo.askCopilot(history, prompt)
        
        // Save bot reply to DB
        insertChatMessage(ChatMessageEntity(message = reply, isUser = false))
        return reply
    }
}
