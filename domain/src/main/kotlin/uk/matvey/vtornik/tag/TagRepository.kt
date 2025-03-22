package uk.matvey.vtornik.tag

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.pool.ConnectionPool
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import kotlinx.coroutines.future.await
import java.time.ZoneOffset.UTC

class TagRepository(
    private val db: ConnectionPool<PostgreSQLConnection>,
) {

    suspend fun findAllByUserIdAndMovieId(userId: Int, movieId: Long): List<Tag> {
        val result = db.sendPreparedStatement(
            "select * from tags where user_id = ? and movie_id = ?",
            listOf(userId, movieId)
        ).await()
        return result.rows.map { toTag(it) }
    }

    fun toTag(data: RowData): Tag {
        return Tag(
            userId = data.getInt("user_id")!!,
            movieId = data.getLong("movie_id")!!,
            tag = data.getString("tag")!!,
            createdAt = data.getDate("created_at")!!.toInstant(UTC),
        )
    }
}