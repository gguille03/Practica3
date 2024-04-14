@file:Suppress("NAME_SHADOWING")

package mx.unam.fi.practica3

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import mx.unam.fi.practica3.ui.theme.Practica3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear una conexión a la base de datos SQLite
        val configuration = SupportSQLiteOpenHelper.Configuration.builder(this)
            .name("my_database.db")
            .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    // Crear la estructura de la tabla al crear la base de datos
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS Person (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "name TEXT, " +
                                "age INTEGER, " +
                                "email TEXT)"
                    )

                    // Insertar registros iniciales
                    insertInitialData(db)
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    // Aquí puedes manejar las actualizaciones de la base de datos si es necesario
                }
            })
            .build()

        val factory = FrameworkSQLiteOpenHelperFactory()
        val helper = factory.create(configuration)
        val db = helper.writableDatabase

        setContent {
            Practica3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android", db)
                }
            }
        }
    }

    // Función para insertar registros iniciales en la tabla
    private fun insertInitialData(db: SupportSQLiteDatabase) {
        val names = listOf("John", "Emma", "Michael", "Sophia", "William")
        val ages = listOf(30, 25, 40, 35, 28)
        val emails = listOf("john@example.com", "emma@example.com", "michael@example.com", "sophia@example.com", "william@example.com")

        for (i in names.indices) {
            val insertQuery = "INSERT INTO Person (name, age, email) VALUES ('${names[i]}', ${ages[i]}, '${emails[i]}')"
            db.execSQL(insertQuery)
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("CoroutineCreationDuringComposition", "Range")
@Composable
fun Greeting(name: String, db: SupportSQLiteDatabase, modifier: Modifier = Modifier) {
    var message by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // Obtener los datos actualizados de la base de datos
        val query = "SELECT name, age, email FROM Person WHERE name = ?"
        val cursor = db.query(query, arrayOf(name))

        message = if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val age = cursor.getInt(cursor.getColumnIndex("age"))
            val email = cursor.getString(cursor.getColumnIndex("email"))

            "Nombre: $name, Edad: $age, Email: $email"
        } else {
            "No se encontró el registro."
        }
    }

    Text(
        text = message,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Practica3Theme {
        //Greeting("Android", null)
    }
}
