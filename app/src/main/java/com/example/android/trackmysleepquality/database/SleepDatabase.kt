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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {

    abstract val sleepDatabaseDao: SleepDatabaseDao //You can have multiple DAOs

    companion object { //The companion object allows clients to access the methods for creating or getting the database without instantiating the class. Since the only purpose of this class is to provide a database, there is no reason to ever instantiate it.
//Inside the companion object, declare a private nullable variable INSTANCE for the database and initialize it to null. The INSTANCE variable will keep a reference to the database, once one has been created. This helps you avoid repeatedly opening connections to the database, which is expensive.
        @Volatile //Annotate INSTANCE with @Volatile. The value of a volatile variable will never be cached, and all writes and reads will be done to and from the main memory. This helps make sure the value of INSTANCE is always up-to-date and the same to all execution threads. It means that changes made by one thread to INSTANCE are visible to all other threads immediately, and you don't get a situation where, say, two threads each update the same entity in a cache, which would create a problem.
        private var INSTANCE: SleepDatabase? = null

        fun getInstance(context: Context): SleepDatabase { //the database builder needs a context parameter.
            synchronized(this) { //Pass in this so that you can access the context.
//Multiple threads can potentially ask for a database instance at the same time, resulting in two databases instead of one. This problem is not likely to happen in this sample app, but it's possible for a more complex app. Wrapping the code to get the database into synchronized means that only one thread of execution at a time can enter this block of code, which makes sure the database only gets initialized once.
                var instance = INSTANCE // takes advantage of smart cast.
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext, //supply the context that you passed in
                            SleepDatabase::class.java, //the database class,
                            "sleep_history_database") //a name for the database
                            .fallbackToDestructiveMigration() //Add the required migration strategy to the builder
                            .build() //Normally, you would have to provide a migration object with a migration strategy for when the schema changes. A migration object is an object that defines how you take all rows with the old schema and convert them to rows in the new schema, so that no data is lost.
                    INSTANCE = instance
                } //A simple solution is to destroy and rebuild the database, which means that the data is lost.

                return instance
            }
        }
    }

}

/**
 * You need to create an abstract database holder class, annotated with @Database. This class has one method that either creates an instance of the database if the database doesn't exist, or returns a reference to an existing database.

Getting a Room database is a bit involved, so here's the general process before you start with the code:

Create a public abstract class that extends RoomDatabase. This class is to act as a database holder. The class is abstract, because Room creates the implementation for you.
Annotate the class with @Database. In the arguments, declare the entities for the database and set the version number.
Inside a companion object, define an abstract method or property that returns a SleepDatabaseDao. Room will generate the body for you.
You only need one instance of the Room database for the whole app, so make the RoomDatabase a singleton.
Use Room's database builder to create the database only if the database doesn't exist. Otherwise, return the existing database.
 */
