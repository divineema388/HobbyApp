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
        // Load data from database
        loadHobbiesFromDatabase()
    }
    
    private fun loadHobbiesFromDatabase() {
        // This will be populated by the database load callback
    }
    
    fun addHobby(name: String, category: String) {
        val newHobby = Hobby(nextId++, name, category)
        _hobbies.add(newHobby)
        SaveToDbTask().execute(SaveOperation.INSERT, newHobby)
    }
    
    fun updateTimeSpent(id: Int, minutes: Int) {
        val index = _hobbies.indexOfFirst { it.id == id }
        if (index != -1) {
            val updatedHobby = _hobbies[index].copy(timeSpent = _hobbies[index].timeSpent + minutes)
            _hobbies[index] = updatedHobby
            SaveToDbTask().execute(SaveOperation.UPDATE, updatedHobby)
        }
    }
    
    fun toggleActive(id: Int) {
        val index = _hobbies.indexOfFirst { it.id == id }
        if (index != -1) {
            val updatedHobby = _hobbies[index].copy(isActive = !_hobbies[index].isActive)
            _hobbies[index] = updatedHobby
            SaveToDbTask().execute(SaveOperation.UPDATE, updatedHobby)
        }
    }
    
    fun deleteHobby(id: Int) {
        val hobbyToDelete = _hobbies.find { it.id == id }
        _hobbies.removeIf { it.id == id }
        if (hobbyToDelete != null) {
            SaveToDbTask().execute(SaveOperation.DELETE, hobbyToDelete)
        }
    }
    
    fun updateLevel(id: Int, newLevel: String) {
        val index = _hobbies.indexOfFirst { it.id == id }
        if (index != -1) {
            val updatedHobby = _hobbies[index].copy(level = newLevel)
            _hobbies[index] = updatedHobby
            SaveToDbTask().execute(SaveOperation.UPDATE, updatedHobby)
        }
    }
    
    // Call this from MainActivity after database is initialized
    fun setHobbiesFromDatabase(loadedHobbies: List<Hobby>) {
        _hobbies.clear()
        _hobbies.addAll(loadedHobbies)
        nextId = (_hobbies.maxOfOrNull { it.id } ?: 0) + 1
    }
}

class MainActivity : ComponentActivity() {
    private val viewModel: HobbyViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize notifications
        NotificationHelper.createNotificationChannel(this)
        
        // Load data from database first
        LoadFromDbTask { hobbies ->
            viewModel.setHobbiesFromDatabase(hobbies)
            setContent {
                HobbyAppTheme {
                    HobbyApp(viewModel)
                }
            }
        }.execute()
    }
}