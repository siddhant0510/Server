package Server.skcoding.di

import Server.skcoding.data.repository.follow.FollowRepository
import Server.skcoding.data.repository.follow.FollowRepositoryImpl
import Server.skcoding.data.repository.likes.LikesRepository
import Server.skcoding.data.repository.likes.LikesRepositoryImpl
import Server.skcoding.data.repository.post.PostRepository
import Server.skcoding.data.repository.post.PostRepositoryImpl
import Server.skcoding.data.repository.user.UserRepository
import Server.skcoding.data.repository.user.UserRepositoryImpl
import Server.skcoding.service.FollowService
import Server.skcoding.service.LikeService
import Server.skcoding.service.PostService
import Server.skcoding.service.UserService
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
    single<UserRepository> {
        UserRepositoryImpl(get())
    }
    single<FollowRepository>{
        FollowRepositoryImpl(get())
    }
    single<PostRepository> {
        PostRepositoryImpl(get())
    }
    single<LikesRepository> {
        LikesRepositoryImpl(get())
    }

    single { UserService(get()) }
    single { FollowService(get()) }
    single { PostService(get()) }
    single { LikeService(get()) }
}