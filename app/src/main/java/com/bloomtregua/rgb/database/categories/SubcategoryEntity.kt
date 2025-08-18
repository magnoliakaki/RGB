package com.bloomtregua.rgb.database.categories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subcategories",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["subcategoryCategoryId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
        ],
    indices = [androidx.room.Index("subcategoryCategoryId")
    ])
data class SubcategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "subcategoryId")
    val subcategoryId: Long = 0,

    @ColumnInfo(name = "subcategoryName")
    val subcategoryName: String,

    @ColumnInfo(name = "subcategoryIcon")
    val subcategoryIcon: String? = null,

    @ColumnInfo(name = "subcategoryCategoryId")
    val subcategoryCategoryId: Long = 0,

    @ColumnInfo(name = "subcategoryAllDefaultAmount")  //Valore di default impostato dall'utente per indicare l'assegnato da dare alla categoria
    val subcategoryAllDefaultAmount: Double? = 0.0,

    @ColumnInfo(name = "subcategoryAllAmount")    // Valore assegnato alla sottocategoria, da valorizzare ad ogni effettivo cambiamento da parte dell'utente o alla valorizzazione automatica a inizio mese
    val subcategoryAllAmount: Double? = null,

    // Le impostazioni di frequenza , tipo allocazione e tutti i dettagli saranno ereditati dalla categoria.
)
