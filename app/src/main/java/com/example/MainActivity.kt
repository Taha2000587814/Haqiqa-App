package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.AppDatabase
import com.example.data.repository.FactCheckRepository
import com.example.ui.screens.HaqiqaMainScreen
import com.example.ui.theme.HaqiqaTheme
import com.example.ui.viewmodel.HaqiqaViewModel
import com.example.ui.viewmodel.HaqiqaViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HaqiqaTheme {
                // Initialize Room Database & Repository directly in Composable or grab from applicationContext
                val context = this.applicationContext
                val database = AppDatabase.getDatabase(context)
                val repository = FactCheckRepository(database.factCheckDao(), context)
                
                // Get ViewModel using our custom Factory
                val factory = HaqiqaViewModelFactory(repository)
                val viewModel: HaqiqaViewModel = viewModel(factory = factory)

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    HaqiqaMainScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
