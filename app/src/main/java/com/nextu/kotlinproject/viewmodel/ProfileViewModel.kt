package com.nextu.kotlinproject.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nextu.kotlinproject.data.local.ProfileDao
import com.nextu.kotlinproject.data.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel(
    app: Application,
    private val dao: ProfileDao
) : AndroidViewModel(app) {

    private val file = File(app.filesDir, "profile.png")

    val profile: StateFlow<Profile?> = dao.observe().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    private val _photo = MutableStateFlow<Bitmap?>(null)
    val photo: StateFlow<Bitmap?> = _photo.asStateFlow()

    init {
        load()
        ensureDefaultProfile()
    }

    fun load() {
        _photo.value = if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
    }

    fun save(bitmap: Bitmap) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        _photo.value = bitmap
    }

    fun saveProfile(prenom: String, nom: String, telephone: String, email: String) {
        viewModelScope.launch {
            dao.upsert(
                Profile(
                    prenom = prenom,
                    nom = nom,
                    telephone = telephone,
                    email = email
                )
            )
        }
    }

    private fun ensureDefaultProfile() {
        viewModelScope.launch {
            if (profile.value == null) {
                dao.upsert(Profile(prenom = "Junior", nom = "", telephone = "", email = ""))
            }
        }
    }
}

class ProfileViewModelFactory(
    private val app: Application,
    private val dao: ProfileDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(app, dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

