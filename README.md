# Track My Sleep Quality 
- An app to track the quality of sleep each night.
- Made with Google Code Labs!

## Components:
- Room 
## Room
- In the database world, you need entities and queries. An entity represents an object or concept, and its properties, to store in the database. An entity class defines a table, and each instance of that class represents a row in the table. A query is a request for data or information from a database table or combination of tables, or a request to perform an action on the data. Common queries are for getting, inserting, and updating entities.
- Room does all the hard work for you to get from Kotlin data classes to entities that can be stored in SQLite tables, and from function declarations to SQL queries.
- You must define each entity as an annotated data class, and the interactions as an annotated interface, a data access object (DAO). Room uses these annotated classes to create tables in the database, and queries that act on the database.
- DAO: On Android, the DAO provides convenience methods for inserting, deleting, and updating the database. When you use a Room database, you query the database by defining and calling Kotlin functions in your code. These Kotlin functions map to SQL queries. You define those mappings in a DAO using annotations,and Room creates the necessary code. Think of a DAO as defining a custom interface for accessing your database. For common database operations, the Room library provides convenience annotations, such as @Insert, @Delete, and @Update. For everything else, there is the @Query annotation. You can write any query that's supported by SQLite. As an added bonus, as you create your queries in Android Studio, the compiler checks your SQL queries for syntax errors.

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



