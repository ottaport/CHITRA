package com.ottaport.chitra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.ottaport.chitra.ui.theme.CHITRATheme
import com.ottaport.chitra.viewmodel.ChitraViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ChitraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CHITRATheme {
                ChitraApp(viewModel = viewModel)
            }
        }
    }
}
