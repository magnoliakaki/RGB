package com.bloomtregua.rgb

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.bloomtregua.rgb.database.RGBDatabase
import com.bloomtregua.rgb.database.prepopulateDatabase
import com.bloomtregua.rgb.layout.homepage.HomePage
import com.bloomtregua.rgb.viewmodels.InitialSetupViewModel
import com.bloomtregua.rgb.viewmodels.CategoriesViewModel
import com.bloomtregua.rgb.ui.theme.RGBTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val initialSetupViewModel: InitialSetupViewModel by viewModels()
    private val categoriesViewModel: CategoriesViewModel by viewModels()
    private var lastProcessedDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        // DA ELIMINARE DIREI QUANDO NON MI SERVE PIU PREPOPOLARE IL DB
//        CoroutineScope(Dispatchers.IO).launch {
//            val database = RGBDatabase.getInstance(applicationContext) as RGBDatabase
//            prepopulateDatabase(applicationContext,database)
//        }

        //TODO : IMPOSTARE I COLORI DI TUTTO IL PROGETTO (E LA GESTIONE DEI TEMI CHIARI / SCURI) E LE STRINGHE IN MANIERA "INTELLIGENTE" USANDO I UI.THEME
        setContent {
            RGBTheme(dynamicColor = false)  {
                Surface() {
                    //AddTransactionPage()
                    HomePage(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Controlla se è un nuovo giorno o se non è stato processato oggi
        // Verifico se ci sono delle transazioni future da dover contabilizzare
        val today = LocalDate.now()
        if (lastProcessedDate == null || !lastProcessedDate!!.isEqual(today)) {
            initialSetupViewModel.triggerPendingTransactionProcessing()
            lastProcessedDate = today
            categoriesViewModel.notifyDataChanged() // Notifico la modifica così si aggiorna la mia homepage
        }
    }
}