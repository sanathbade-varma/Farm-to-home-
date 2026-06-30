package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientAddressDao {
    @Query("SELECT * FROM client_addresses ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ClientAddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ClientAddressEntity)

    @Query("DELETE FROM client_addresses WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface FarmerRegistrationDao {
    @Query("SELECT * FROM farmer_registrations ORDER BY timestamp DESC")
    fun getAll(): Flow<List<FarmerRegistrationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FarmerRegistrationEntity)

    @Query("DELETE FROM farmer_registrations WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface DeliveryAgentRegistrationDao {
    @Query("SELECT * FROM delivery_agent_registrations ORDER BY timestamp DESC")
    fun getAll(): Flow<List<DeliveryAgentRegistrationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DeliveryAgentRegistrationEntity)

    @Query("DELETE FROM delivery_agent_registrations WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface SellerAddressDao {
    @Query("SELECT * FROM seller_addresses ORDER BY timestamp DESC")
    fun getAll(): Flow<List<SellerAddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SellerAddressEntity)

    @Query("DELETE FROM seller_addresses WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAll(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAll()
}

@Database(
    entities = [
        ClientAddressEntity::class,
        FarmerRegistrationEntity::class,
        DeliveryAgentRegistrationEntity::class,
        SellerAddressEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientAddressDao(): ClientAddressDao
    abstract fun farmerRegistrationDao(): FarmerRegistrationDao
    abstract fun deliveryAgentRegistrationDao(): DeliveryAgentRegistrationDao
    abstract fun sellerAddressDao(): SellerAddressDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "farm_to_home_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
