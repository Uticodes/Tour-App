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

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eu.tutorials.tourguideapp.data.Tour
import kotlin.random.Random

class ToursViewModel(val dataSource: DataSource) : ViewModel() {

    val toursLiveData = dataSource.getTourList()

    /* If the name and description are present, create new Flower and add it to the datasource */
    fun insertTour(countryName: String?, placeName: String?, date: String, description: String?, placeImage: Int?) {
        if (countryName == null || placeName == null || description == null) {
            return
        }

        val image = dataSource.getRandomTourImageAsset()
        val newFlower = Tour(
            Random.nextLong(),
            countryName,
            placeName,
            date,
            description,
            image.toString().toInt(),
        )

        dataSource.addTour(newFlower)
    }
}

class ToursViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToursViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ToursViewModel(
                dataSource = DataSource.getDataSource(context.resources)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}