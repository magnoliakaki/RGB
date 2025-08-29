package com.bloomtregua.rgb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bloomtregua.rgb.database.RGBDatabase
import com.bloomtregua.rgb.database.prepopulateDatabase
import com.bloomtregua.rgb.layout.addtransactionpage.AddTransactionPage
import com.bloomtregua.rgb.layout.homepage.HomePage
import com.bloomtregua.rgb.navigation.AppDestinations
import com.bloomtregua.rgb.ui.theme.RGBTheme
import com.bloomtregua.rgb.viewmodels.CategoriesViewModel
import com.bloomtregua.rgb.viewmodels.InitialSetupViewModel
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

//      // DA ELIMINARE DIREI QUANDO NON MI SERVE PIU PREPOPOLARE IL DB
        //TODO: LO LASCIO MA CON IF(FALSE) PERCHÈ SE LO TOLGO ANDROID RIMUOVE LE DIPENDENZE IN IMPORT
        if(false) {
            CoroutineScope(Dispatchers.IO).launch {
                val database = RGBDatabase.getInstance(applicationContext) as RGBDatabase
                prepopulateDatabase(applicationContext, database)
            }
        }

        //TODO : IMPOSTARE I COLORI DI TUTTO IL PROGETTO (E LA GESTIONE DEI TEMI CHIARI / SCURI) E LE STRINGHE IN MANIERA "INTELLIGENTE" USANDO I UI.THEME
        setContent {
            RGBTheme(dynamicColor = false)  {
                Surface() {
                    AppNavigation() // Chiama il tuo Composable di navigazione
                    //PER ILARIA: SE VUOI IMPOSTARE COME DEFAULT IL TUO TRANSACTIONPAGE, sotto invece di mettere AppDestinations.HOME_ROUTE metti AppDestinations.ADD_TRANSACTION_ROUTE come default
                }
            }
        }
    }

    @Composable
    fun AppNavigation() {
        val navController = rememberNavController() // Crea e ricorda il NavController

        NavHost(
            navController = navController,
            startDestination = AppDestinations.HOME_ROUTE // La schermata iniziale
        ) {
            composable(AppDestinations.HOME_ROUTE) {
                // Passa il navController a HomePage se HomePage ha bisogno di navigare altrove
                HomePage(modifier = Modifier.fillMaxSize(), navController = navController)
            }
            composable(AppDestinations.ADD_TRANSACTION_ROUTE) {
                // Passa il navController a AddTransactionPage se ha bisogno di navigare
                // o per tornare indietro programmaticamente (anche se il back button di sistema funziona già)
                AddTransactionPage(modifier = Modifier.fillMaxSize(), navController = navController)
            }
            // Aggiungi altre destinazioni qui con composable(route) { TuoComposableSchermata() }
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