package com.bloomtregua.rgb.database.util


interface DatabaseTransactionRunner {
    /**
     * Esegue il blocco di codice fornito all'interno di una singola transazione database.
     * Se il blocco ha successo, la transazione viene commessa.
     * Se il blocco lancia un'eccezione, la transazione viene annullata (rollback).
     */
    suspend fun <R> runInTransaction(block: suspend () -> R): R
}