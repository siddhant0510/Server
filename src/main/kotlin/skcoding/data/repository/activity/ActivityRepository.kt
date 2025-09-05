package Server.skcoding.data.repository.activity

import Server.skcoding.data.models.Activity
import Server.skcoding.util.Constants

interface ActivityRepository {

    suspend fun getActivitiesRepository(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_ACTIVITY_PAGE_SIZE
    ): List<Activity>

    suspend fun createActivity(activity: Activity)

    suspend fun deleteActivity(activityId: String): Boolean
}