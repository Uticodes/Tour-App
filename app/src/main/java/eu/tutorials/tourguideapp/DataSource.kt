package eu.tutorials.tourguideapp
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import eu.tutorials.tourguideapp.data.Tour
import eu.tutorials.tourguideapp.data.TourList

/* Handles operations on toursLiveData and holds details about it. */
class DataSource(resources: Resources) {
    private val initialTourList = TourList.tourList
    private val toursLiveData = MutableLiveData(initialTourList)

    /* Adds tour to liveData and posts value. */
    fun addTour(tour: Tour) {
        val currentList = toursLiveData.value
        if (currentList == null) {
            toursLiveData.postValue(listOf(tour))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, tour)
            toursLiveData.postValue(updatedList)
        }
    }

    /* Removes tour from liveData and posts value. */
    fun removeTour(tour: Tour) {
        val currentList = toursLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(tour)
            toursLiveData.postValue(updatedList)
        }
    }

    /* Returns tour given an ID. */
    fun getTourForId(id: Long): Tour? {
        toursLiveData.value?.let { tours ->
            return tours.firstOrNull{ it.id == id}
        }
        return null
    }

    fun getTourList(): LiveData<List<Tour>> {
        return toursLiveData
    }

    /* Returns a random tour asset for tours that are added. */
    fun getRandomTourImageAsset(): Int? {
        val randomNumber = (initialTourList.indices).random()
        return initialTourList[randomNumber].placeImage
    }

    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(resources: Resources): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE ?: DataSource(resources)
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}