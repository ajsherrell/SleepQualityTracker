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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        //You need a reference to the app that this fragment is attached to, to pass into the view-model factory provider.
        //The requireNotNull Kotlin function throws an IllegalArgumentException if the value is null.
        val application = requireNotNull(this.activity).application

        //You need a reference to your data source via a reference to the DAO.
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        // create an instance of the viewModelFactory. You need to pass it dataSource and the application
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)

        //Now that you have a factory, get a reference to the SleepTrackerViewModel. The SleepTrackerViewModel::class.java parameter refers to the runtime Java class of this object
        val sleepTrackerViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SleepTrackerViewModel::class.java)
        binding.sleepTrackerViewModel = sleepTrackerViewModel

        //The SleepTrackerFragment needs to observe _navigateToSleepQuality so that the app knows when to navigate.
        sleepTrackerViewModel.navigateToSleepQuality.observe(this, Observer { night ->
            night?.let {
                this.findNavController().navigate(
                        SleepTrackerFragmentDirections
                                .actionSleepTrackerFragmentToSleepQualityFragment(night.nightId))
                sleepTrackerViewModel.doneNavigating()
            }
        })

        //Set the current activity as the lifecycle owner of the binding
        binding.setLifecycleOwner(this)

        return binding.root
    }

}
