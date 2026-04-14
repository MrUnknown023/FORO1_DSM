package com.sv.udb.registronotasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sv.udb.registronotasapp.ui.navigation.AppNavGraph
import com.sv.udb.registronotasapp.ui.theme.RegistroNotasAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RegistroNotasAppTheme {
                AppNavGraph()
            }
        }
    }
}