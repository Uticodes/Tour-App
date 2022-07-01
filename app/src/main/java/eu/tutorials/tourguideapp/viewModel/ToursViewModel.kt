/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.tutorials.tourguideapp.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import eu.tutorials.tourguideapp.repository.FirestoreRepository
import eu.tutorials.tourguideapp.models.Tour
import eu.tutorials.tourguideapp.models.User
import eu.tutorials.tourguideapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToursViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseRepository = FirestoreRepository()
    private val genericLiveData = MutableLiveData<Resource<String>>()

    init {
        viewModelScope.launch {

        }
    }

    fun registerUser(
        userName: String,
        email: String,
        password: String
    ): LiveData<Resource<String>> {
        genericLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            genericLiveData.postValue(firebaseRepository.registerUser(userName, email, password))
        }
        return genericLiveData
    }

    fun loginUser(email: String, password: String): LiveData<Resource<String>> {
        genericLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            genericLiveData.postValue(firebaseRepository.loginUser(email, password))
        }
        return genericLiveData
    }

    fun getUserInfo(): LiveData<Resource<User>> {
        val genericLiveData = MutableLiveData<Resource<User>>()
        genericLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            genericLiveData.postValue(firebaseRepository.getUserInfo())
        }
        return genericLiveData
    }

    fun addTour(tour: Tour, selectedImageUri: Uri): LiveData<Resource<String>> {
        val genericLiveData = MutableLiveData<Resource<String>>()
        genericLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            genericLiveData.postValue(firebaseRepository.uploadImageToFirebaseStorageAndAddTour(tour, selectedImageUri))
        }
        return genericLiveData
    }

     fun getTours(): LiveData<Resource<List<Tour>>> {
        val genericLiveData = MutableLiveData<Resource<List<Tour>>>()
        genericLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            genericLiveData.postValue(firebaseRepository.getTours())
        }
        return genericLiveData
    }

    fun getUserTourFeeds(): LiveData<Resource<List<Tour>>> {
        val genericLiveData = MutableLiveData<Resource<List<Tour>>>()
        genericLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            genericLiveData.postValue(firebaseRepository.getUserTourFeeds())
        }
        return genericLiveData
    }

    fun editTour(tour: Tour): LiveData<Resource<String>> {
        val genericLiveData = MutableLiveData<Resource<String>>()
        genericLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            genericLiveData.postValue(firebaseRepository.editTour(tour))
        }
        return genericLiveData
    }

    fun deleteATour(docId: String): LiveData<Resource<String>> {
        val genericLiveData = MutableLiveData<Resource<String>>()
        genericLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            genericLiveData.postValue(firebaseRepository.deleteATour(docId))
        }
        return genericLiveData
    }

    fun logOut(): LiveData<Resource<String>> {
        val genericLiveData = MutableLiveData<Resource<String>>()
        genericLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            genericLiveData.postValue(firebaseRepository.logOut())
        }
        return genericLiveData
    }
}
