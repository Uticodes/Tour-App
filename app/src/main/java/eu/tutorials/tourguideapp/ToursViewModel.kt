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

package eu.tutorials.tourguideapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import eu.tutorials.tourguideapp.data.Tour
import eu.tutorials.tourguideapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class ToursViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseRepository = FirestoreImplementations()
    private val genericLiveData = MutableLiveData<Resource<String>>()

    private val fsImpl: FirestoreImplementations = FirestoreImplementations()
    private val _requestStateLiveData = MutableLiveData<Resource<Unit>>()
    val requestStateLiveData: LiveData<Resource<Unit>> = _requestStateLiveData

    private val _tours = MutableLiveData<ArrayList<Tour>>()
    val tours: LiveData<ArrayList<Tour>> = _tours

    init {
        viewModelScope.launch {
            //_tours.value = firebaseRepository.getUserTourFeeds(ArrayList<Tour>?, requestStateLiveData)
           // _posts.value = FirebaseProfileService.getPosts()
        }
    }

    fun registerUser(userName: String, email: String, password: String): LiveData<Resource<String>> {
        genericLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            //genericLiveData.postValue(firebaseRepository.registerUser(email, password))
        }
        return genericLiveData
    }


//    private var dirPath: String
//    private var fileName: String


//    val toursLiveData = fsImpl.registerUser(
//        userName: String,
//        userEmail: String,
//        userPassword: String,
//        result: (Resource<Unit>) -> Unit,
//    context: LoginActivity
//    )

//    fun registerUser(
//        userName: String,
//        userEmail: String,
//        userPassword: String,
//        result: (Resource<Unit>) -> Unit,
//        //context: LoginActivity
//    ){
//
//    }
//
//    /* If the name and description are present, create new Flower and add it to the datasource */
//    fun insertTour(countryName: String?, placeName: String?, date: String, description: String?, placeImage: Int?) {
//        if (countryName == null || placeName == null || description == null) {
//            return
//        }
//
//        val image = dataSource.getRandomTourImageAsset()
//        val newFlower = Tour(
//            Random.nextLong(),
//            countryName,
//            placeName,
//            date,
//            description,
//            image.toString(),
//        )
//
//        dataSource.addTour(newFlower)
//    }
//
//    class Factory(private val fsImpl: FirestoreImplementations) : ViewModelProvider.Factory {
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            //return ToursViewModel(fsImpl) as T
//        }
//    }
}

//class ToursViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ToursViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return ToursViewModel(
//                dataSource = DataSource.getDataSource(context.resources)
//            ) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}