package com.hobby.dealabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hobby.dealabs.ui.theme.HobbyAppTheme

data class Hobby(
    val id: Int,
    val name: String,
    val category: String,
    val timeSpent: Int = 0, // in minutes
    val level: String = "Beginner",
    val isActive: Boolean = true
)

class HobbyViewModel : ViewModel() {
    private val _hobbies = mutableStateListOf<Hobby>()
    val hobbies: List<Hobby> = _hobbies
    
    private var nextId = 1
    
    init {
        // Add some sample data
        addHobby("Photography", "Creative")
        addHobby("Guitar Playing", "Music")
        addHobby("Gardening", "Outdoor")
    }
    
    fun addHobby(name: String, category: String) {
        _hobbies.add(Hobby(nextId++, name, category))
    }
    
    fun updateTimeSpent(id: Int, minutes: Int) {
        val index = _hobbies.indexOfFirst { it.id == id }
        if (index != -1) {
            _hobbies[index] = _hobbies[index].copy(timeSpent = _hobbies[index].timeSpent + minutes)
        }
    }
    
    fun toggleActive(id: Int) {
        val index = _hobbies.indexOfFirst { it.id == id }
        if (index != -1) {
            _hobbies[index] = _hobbies[index].copy(isActive = !_hobbies[index].isActive)
        }
    }
    
    fun deleteHobby(id: Int) {
        _hobbies.removeIf { it.id == id }
    }
    
    fun updateLevel(id: Int, newLevel: String) {
        val index = _hobbies.indexOfFirst { it.id == id }
        if (index != -1) {
            _hobbies[index] = _hobbies[index].copy(level = newLevel)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize notifications
        NotificationHelper.createNotificationChannel(this)
        
        setContent {
            HobbyAppTheme {
                val viewModel: HobbyViewModel = viewModel()
                HobbyApp(viewModel)
            }
        }
    }
}