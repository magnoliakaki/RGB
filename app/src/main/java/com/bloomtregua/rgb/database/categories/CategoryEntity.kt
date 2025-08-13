package com.bloomtregua.rgb.database.categories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bloomtregua.rgb.database.accounts.AccountEntity
import com.bloomtregua.rgb.database.allocations.AllocationEntity
import java.time.LocalDate

@Entity(tableName = "categories",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = MacroCategoryEntity::class,
            parentColumns = ["macroCategoryId"],
            childColumns = ["categoryMacroCategoryId"],
            onDelete = androidx.room.ForeignKey.SET_NULL
        ),
        androidx.room.ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["accountId"],
            childColumns = ["categoryAccountId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        ),
        androidx.room.ForeignKey(
            entity = AllocationEntity::class,
            parentColumns = ["allocationId"],
            childColumns = ["categoryAllocationId"],
            onDelete = androidx.room.ForeignKey.SET_DEFAULT
        )
],
    indices = [
        androidx.room.Index("categoryMacroCategoryId"),
        androidx.room.Index("categoryAccountId"),
        androidx.room.Index("categoryAllocationId")]
    )
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "categoryId")
    val categoryId: Long = 0,

    @ColumnInfo(name = "categoryName")
    val categoryName: String,

    // Sulla homepage mostro solamente le categorie con valore FALSE
    @ColumnInfo(name = "categoryIncome")    // Vale false se è categoria di uscita altrimenti true se è di entrata soldi
    val categoryIncome: Boolean = false,

    @ColumnInfo(name = "categoryIcon")
    val categoryIcon: String? = null,

    @ColumnInfo(name = "categoryMacroCategoryId")
    val categoryMacroCategoryId: Long? = null,

    @ColumnInfo(name = "categoryAccountId") // Ogni categoria è associata ad un conto
    val categoryAccountId: Long,

    @ColumnInfo(name = "categoryAllocationId")  // Tipo di allocazione (tabella allocations)
    val categoryAllocationId: Long? = null,

    @ColumnInfo(name = "categoryAllEndDate")
    val categoryAllEndDate: LocalDate? = null,

    @ColumnInfo(name = "categoryAllEndAmount")
    val categoryAllEndAmount: Double? = null,

    @ColumnInfo(name = "categoryAllDefaultAmount")  //Valore di default impostato dall'utente per indicare l'assegnato da dare alla categoria
    val categoryAllDefaultAmount: Double? = 0.0,

    @ColumnInfo(name = "categoryAllAmount")     // Valore assegnato alla categoria, da valorizzare ad ogni effettivo cambiamento da parte dell'utente o alla valorizzazione automatica a inizio mese
    val categoryAllAmount: Double? = null,

    @ColumnInfo(name = "categoryAllFrequencyDays")
    val categoryAllFrequencyDays: Int? = null,

    @ColumnInfo(name = "categoryAllFrequencyWeeks")
    val categoryAllFrequencyWeeks: Int? = null,

    @ColumnInfo(name = "categoryAllFrequencyMonths")
    val categoryAllFrequencyMonths: Int? = null,

    @ColumnInfo(name = "categoryAllFrequencyYears")
    val categoryAllFrequencyYears: Int? = null
)
