package com.bloomtregua.rgb.database.budget

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bloomtregua.rgb.database.categories.CategoryEntity

@Entity(tableName = "budgets",
    foreignKeys = [androidx.room.ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["categoryId"],
        childColumns = ["budgetSurplusCategoryId"],
        onDelete = androidx.room.ForeignKey.SET_DEFAULT
    ), androidx.room.ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["categoryId"],
        childColumns = ["budgetResetCategory"],
        onDelete = androidx.room.ForeignKey.SET_DEFAULT
    )],
    indices = [androidx.room.Index("budgetSurplusCategoryId"),
        androidx.room.Index("budgetResetCategory"),
        androidx.room.Index(value = ["lock_column"], unique = true)
    ]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "budgetId")
    val budgetId: Long = 0,

    @ColumnInfo(name = "budgetName")
    val budgetName: String,

    // CATEGORY o DATE --> NB: Questa tabella avrà  UN SOLO RECORD, che sarà l'indicazione di quando o come andare a resettare il budget.
    @ColumnInfo(name = "budgetResetType")
    val budgetResetType: BudgetResetType = BudgetResetType.DATE,

    // Categoria di entrata (transazione di entrata di quella specifica categoria) che scatena il reset. Valido solo se il campo sopra vale CATEGORY
    @ColumnInfo(name = "budgetResetCategory")
    val budgetResetCategory: Long? = null,

    // Giorno di reset, vedi poi le logiche sotto per identificare il giorno esatto nel mese.
    @ColumnInfo(name = "budgetResetDay")
    val budgetResetDay: Int? = null,

    // Indica se il giorno di reset viene di fine settimana (sabato o domenica) allora deve prendere quel giorno esatto (false) oppure fare uno shift al lunedì successivo o venerdì precedente.
    @ColumnInfo(name = "budgetWeekendShift")
    val budgetWeekendShift: Boolean? = null,

    //Direzione dello shift (shift avanti o indietro), se il valore sopra è a true allora si usa questo valore
    @ColumnInfo(name = "budgetWeekendShiftDirection")
    val budgetWeekendShiftDirection: BudgetWeekendShiftDirection? = null,

    @ColumnInfo(name = "budgetAutomaticAllocation")
    val budgetAutomaticAllocation: Boolean = false,

    // Se alla data di Reset ho ancora un valore residuo sulla categoria, cosa deve fare al reset
    @ColumnInfo(name = "budgetSurplusType")
    val budgetSurplusType: BudgetSurplusType = BudgetSurplusType.ROLLOVER,

    // Categoria in caso di Surplus
    @ColumnInfo(name = "budgetSurplusCategoryId")
    val budgetSurplusCategoryId: Long? = null,

    //Uso questa colonna per GARANTIRE che possa esistere sempre e solo 1 unico record in questa tabella.
    @ColumnInfo(name = "lock_column", defaultValue = "1")
    val lockColumn: Int = 1 // Il valore può essere qualsiasi cosa, basta che sia costante.

)
