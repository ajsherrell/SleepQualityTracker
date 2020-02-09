/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) { //AndroidViewModel is the same as ViewModel, but it takes the application context as a parameter and makes it available as a property.

    //Job allows you to cancel all coroutines started by this view model when the view model is no longer used and is destroyed. This way, you don't end up with coroutines that have nowhere to return to.
    private var viewModelJob = Job()

    /**
     * The scope determines what thread the coroutine will run on, and the scope also needs to know about the job. To get a scope, ask for an instance of CoroutineScope, and pass in a dispatcher and a job.
    Using Dispatchers.Main means that coroutines launched in the uiScope will run on the main thread. This is sensible for many coroutines started by a ViewModel, because after these coroutines perform some processing, they result in an update of the UI.
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val nights = database.getAllNight() //get all nights from the database and assign them to the nights variable.

    //transform nights into a nightsString throught formatNights() from Util.kt
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

    private var tonight = MutableLiveData<SleepNight?>() //holds the current night.

    init {
        initializeTonight()
    }

    private fun initializeTonight() { //launch a coroutine.
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? { //returns a nullable SleepNight, if there is no current started SleepNight.
        //return the result from a coroutine that runs in the Dispatchers.IO context. Use the I/O dispatcher, because getting data from the database is an I/O operation and has nothing to do with the UI.
        return withContext(Dispatchers.IO) {
            //let the coroutine get tonight (the newest night) from the database
            var night = database.getTonight()
            //If the start and end times are not the same, meaning that the night has already been completed, return null. Otherwise, return the night.
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            night
        }
    }

    // click handlers
    fun onStartTracking() {
        //launch a coroutine in the uiScope, because you need this result to continue and update the UI
        uiScope.launch {
            //captures the current time as the start time.
            val newNight = SleepNight()
            insert(newNight) //insert newNight into the database --not the DAO function of the same name.
            tonight.value = getTonightFromDatabase() //update tonight.
        }
    }

    private suspend fun insert(night: SleepNight) {
        //launch a coroutine in the I/O context and insert the night into the database by calling insert() from the DAO
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    fun onStopTracking() { //same as onStartTracking definition
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch //In Kotlin, the return@label syntax specifies the function from which this statement returns, among several nested functions.
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
        }
    }

    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night) //update from DAO definition
        }
    }

    fun onClear() {
        uiScope.launch {
            clear()
            tonight.value = null
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }


    override fun onCleared() { //cancel all coroutines.
        super.onCleared()
        viewModelJob.cancel()
    }

}

/**Now you can see a pattern:

Launch a coroutine that runs on the main or UI thread, because the result affects the UI.
Call a suspend function to do the long-running work, so that you don't block the UI thread while waiting for the result.
The long-running work has nothing to do with the UI. Switch to the I/O context, so that the work can run in a thread pool that's optimized and set aside for these kinds of operations.
Then call the database function to do the work.
The pattern is shown below.

fun someWorkNeedsToBeDone {
uiScope.launch {

suspendFunction()

}
}

suspend fun suspendFunction() {
withContext(Dispatchers.IO) {
longrunningWork()
}
}
 */

/**In the SleepTrackerViewModel, the nights variable references LiveData because getAllNights() in the DAO returns LiveData.

It is a Room feature that every time the data in the database changes, the LiveData nights is updated to show the latest data. You never need to explicitly set the LiveData or update it. Room updates the data to match the database.

However, if you display nights in a text view, it will show the object reference. To see the contents of the object, transform the data into a formatted string. Use a Transformation map that's executed every time nights receives new data from the database.
LOOK INSIDE UTIL.KT*/
