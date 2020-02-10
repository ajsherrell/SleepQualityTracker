# Track My Sleep Quality 
- An app to track the quality of sleep each night.
- Made with Google Code Labs!

## Components:
- Room 
- Coroutines

## Room
- In the database world, you need entities and queries. An entity represents an object or concept, and its properties, to store in the database. An entity class defines a table, and each instance of that class represents a row in the table. A query is a request for data or information from a database table or combination of tables, or a request to perform an action on the data. Common queries are for getting, inserting, and updating entities.
- Room does all the hard work for you to get from Kotlin data classes to entities that can be stored in SQLite tables, and from function declarations to SQL queries.
- You must define each entity as an annotated data class, and the interactions as an annotated interface, a data access object (DAO). Room uses these annotated classes to create tables in the database, and queries that act on the database.
- DAO: On Android, the DAO provides convenience methods for inserting, deleting, and updating the database. When you use a Room database, you query the database by defining and calling Kotlin functions in your code. These Kotlin functions map to SQL queries. You define those mappings in a DAO using annotations,and Room creates the necessary code. Think of a DAO as defining a custom interface for accessing your database. For common database operations, the Room library provides convenience annotations, such as @Insert, @Delete, and @Update. For everything else, there is the @Query annotation. You can write any query that's supported by SQLite. As an added bonus, as you create your queries in Android Studio, the compiler checks your SQL queries for syntax errors.

## Coroutines
- Coroutines use a seperate thread to run database operations. Tapping any of the buttons triggers a database operation, such as creating or updating a SleepNight. For this reason and others, you use coroutines to implement click handlers for the app's buttons.
- Use ViewModel, ViewModelFactory, and data binding to set up the UI architecture for the app.
To keep the UI running smoothly, use coroutines for long-running tasks, such as all database operations.
Coroutines are asynchronous and non-blocking. They use suspend functions to make asynchronous code sequential.
When a coroutine calls a function marked with suspend, instead of blocking until that function returns like a normal function call, it suspends execution until the result is ready. Then it resumes where it left off with the result.
The difference between blocking and suspending is that if a thread is blocked, no other work happens. If the thread is suspended, other work happens until the result is available.
- To launch a coroutine, you need a job, a dispatcher, and a scope:

Basically, a job is anything that can be canceled. Every coroutine has a job, and you can use a job to cancel a coroutine.
The dispatcher sends off coroutines to run on various threads. Dispatcher.Main runs tasks on the main thread, and Dispartcher.IO is for offloading blocking I/O tasks to a shared pool of threads.
The scope combines information, including a job and dispatcher, to define the context in which the coroutine runs. Scopes keep track of coroutines.
- To implement click handlers that trigger database operations, follow this pattern:

Launch a coroutine that runs on the main or UI thread, because the result affects the UI.
Call a suspend function to do the long-running work, so that you don't block the UI thread while waiting for the result.
The long-running work has nothing to do with the UI, so switch to the I/O context. That way, the work can run in a thread pool that's optimized and set aside for these kinds of operations.
Then call the database function to do the work.
Use a Transformations map to create a string from a LiveData object every time the object changes.

- Testing:
Here's a quick run-through of the testing code, because it's another piece of code that you can reuse:

SleepDabaseTest is a test class.
The @RunWith annotation identifies the test runner, which is the program that sets up and executes the tests.
During setup, the function annotated with @Before is executed, and it creates an in-memory SleepDatabase with the SleepDatabaseDao. "In-memory" means that this database is not saved on the file system and will be deleted after the tests run.
Also when building the in-memory database, the code calls another test-specific method, allowMainThreadQueries. By default, you get an error if you try to run queries on the main thread. This method allows you to run tests on the main thread, which you should only do during testing.
In a test method annotated with @Test, you create, insert, and retrieve a SleepNight, and assert that they are the same. If anything goes wrong, throw an exception. In a real test, you would have multiple @Test methods.
When testing is done, the function annotated with @After executes to close the database.
Right-click on the test file in the Project pane and select Run 'SleepDatabaseTest'.
After the tests run, verify in the SleepDatabaseTest pane that all the tests have passed.
Because all the tests passed, you now know several things:

The database gets created correctly.
You can insert a SleepNight into the database.
You can get back the SleepNight.
The SleepNight has the correct value for the quality.

#### Summary
-  Implementing sleep quality tracking in this app is like playing a familiar piece of music in a new key. While details change, the underlying pattern of what you did in previous codelabs in this lesson remains the same. Being aware of these patterns makes coding much faster, because you can reuse code from existing apps. Here are some of the patterns used in this course so far:

Create a ViewModel and a ViewModelFactory and set up a data source.
Trigger navigation. To separate concerns, put the click handler in the view model and the navigation in the fragment.
Use encapsulation with LiveData to track and respond to state changes.
Use transformations with LiveData.
Create a singleton database.
Set up coroutines for database operations.

- Triggering navigation:

You define possible navigation paths between fragments in a navigation file. There are some different ways to trigger navigation from one fragment to the next. These include:

Define onClick handlers to trigger navigation to a destination fragment.
Alternatively, to enable navigation from one fragment to the next:
Define a LiveData value to record if navigation should occur.
Attach an observer to that LiveData value.
Your code then changes that value whenever navigation needs to be triggered or is complete.

- Setting the android:enabled attribute:

The android:enabled attribute is defined in TextView and inherited by all subclasses, including Button.
The android:enabled attribute determines whether or not a View is enabled. The meaning of "enabled" varies by subclass. For example, a non-enabled EditText prevents the user from editing the contained text, and a non-enabled Button prevents the user from tapping the button.
The enabled attribute is not the same as the visibility attribute.
You can use transformation maps to set the value of the enabled attribute of buttons based on the state of another object or variable.

- Other points covered in this codelab:

To trigger notifications to the user, you can use the same technique as you use to trigger navigation.
You can use a Snackbar to notify the user.



