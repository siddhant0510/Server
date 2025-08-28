package Server.skcoding.di

import Server.skcoding.controller.user.UserController
import Server.skcoding.controller.user.UserControllerImpl
import Server.skcoding.util.Constants
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo


//val mainModule = module {
//    single {
//        val client = KMongo.createClient().coroutine
//        client.getDatabase(Constants.DATABASE_NAME)
//    }
//    single<UserController> {
//        UserControllerImpl(get())
//    }
//}

val mainModule = module {
    single {
        val connectionString = "mongodb+srv://siddhantkudale18:Siddhant1212@cluster0.mtbxmxw.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
        val client = KMongo.createClient(connectionString).coroutine
        client.getDatabase("your_database_name_here") // Replace with your actual database name
    }

    single<UserController> {
        UserControllerImpl(get())
    }
}