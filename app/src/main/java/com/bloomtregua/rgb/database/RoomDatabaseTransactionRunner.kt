package com.bloomtregua.rgb.database

import androidx.room.withTransaction
import com.bloomtregua.rgb.database.util.DatabaseTransactionRunner
import javax.inject.Inject

class RoomDatabaseTransactionRunner @Inject constructor(
    private val db: RGBDatabase // Inietta l'istanza del tuo database Room
) : DatabaseTransactionRunner {

    override suspend fun <R> runInTransaction(block: suspend () -> R): R {
        return db.withTransaction(block)
    }
}