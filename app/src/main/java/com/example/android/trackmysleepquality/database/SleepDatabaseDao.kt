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

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SleepDatabaseDao { //All DAOs need to be annotated with the @Dao keyword.

    @Insert
    fun insert(nigh: SleepNight) // When you call insert() from your Kotlin code, Room executes a SQL query to insert the entity into the database.

    @Update
    fun update(nigh: SleepNight) //The entity that's updated is the entity that has the same key as the one that's passed in. You can update some or all of the entity's other properties.

    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key") //There is no convenience annotation for the remaining functionality, so you have to use the @Query annotation and supply SQLite queries.
    fun get(key: Long): SleepNight? //Notice the :key. You use the colon notation in the query to reference arguments in the function.

    @Query("DELETE FROM daily_sleep_quality_table") //The @Delete annotation is great for deleting specific entries, but not efficient for clearing all entries from a table.
    fun clear()

    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1") //To get "tonight" from the database, write a SQLite query that returns the first element of a list of results ordered by nightId in descending order. Use LIMIT 1 to return only one element.
    fun  getTonight(): SleepNight? //Make the SleepNight returned by getTonight() nullable, so that the function can handle the case where the table is empty. (The table is empty at the beginning, and after the data is cleared.)

    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC") //Have the SQLite query return all columns from the daily_sleep_quality_table, ordered in descending order.
    fun getAllNight(): LiveData<List<SleepNight>> //Room keeps this LiveData updated for you, which means you only need to explicitly get the data once.
}
