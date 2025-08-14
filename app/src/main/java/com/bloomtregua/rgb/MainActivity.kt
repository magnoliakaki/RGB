package com.bloomtregua.rgb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.bloomtregua.rgb.database.RGBDatabase
import com.bloomtregua.rgb.database.prepopulateDatabase
import com.bloomtregua.rgb.layout.homepage.HomePage
import com.bloomtregua.rgb.ui.theme.RGBTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // DA ELIMINARE DIREI QUANDO NON MI SERVE PIU PREPOPOLARE IL DB
        CoroutineScope(Dispatchers.IO).launch {
            val database = RGBDatabase.getInstance(applicationContext) as RGBDatabase
            prepopulateDatabase(database)
        }

        //TODO : IMPOSTARE I COLORI DI TUTTO IL PROGETTO (E LA GESTIONE DEI TEMI CHIARI / SCURI) E LE STRINGHE IN MANIERA "INTELLIGENTE" USANDO I UI.THEME
        setContent {
            RGBTheme(dynamicColor = false)  {
                Surface() {
                    HomePage(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}