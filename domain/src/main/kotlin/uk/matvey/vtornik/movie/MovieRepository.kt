package uk.matvey.vtornik.movie

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.pool.ConnectionPool
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import kotlinx.coroutines.future.await
import kotlinx.serialization.json.Json
import java.time.ZoneOffset.UTC

class MovieRepository(
    private val db: ConnectionPool<PostgreSQLConnection>,
) {

    suspend fun findById(id: Long): Movie? {
        return db.sendPreparedStatement(
            "select * from movies where id = ?",
            listOf(id)
        ).await().rows.singleOrNull()?.let { toMovie(it) }
    }

    suspend fun findAllByIds(ids: List<Long>): List<Movie> {
        return db.sendPreparedStatement("select * from movies where id = any(?)", listOf(ids))
            .await().rows.map { toMovie(it) }
    }

    suspend fun add(details: Movie.Details.Tmdb): Long {
        return db.sendPreparedStatement(
            """
                |insert into movies (id, title, year, details, created_at, updated_at)
                | values (?, ?, ?, ?, now(), now())
                | returning id
                |""".trimMargin(),
            listOf(
                details.id,
                details.title,
                details.releaseDateOrNull()?.year,
                Json.encodeToString(Movie.Details(tmdb = details)),
            )
        ).await().rows.single().getLong("id")!!
    }

    private fun toMovie(data: RowData): Movie {
        val details = Json.decodeFromString<Movie.Details>(data.getString("details")!!)
        return Movie(
            id = data.getLong("id")!!,
            title = data.getString("title")!!,
            year = details.tmdb?.releaseDateOrNull()?.year,
            details = details,
            createdAt = data.getDate("created_at")!!.toInstant(UTC),
            updatedAt = data.getDate("updated_at")!!.toInstant(UTC),
        )
    }
}