package uk.matvey.vtornik.movie

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import kotlinx.serialization.json.Json
import uk.matvey.slon.sql.execute
import uk.matvey.slon.sql.getDateOrFail
import uk.matvey.slon.sql.getIntOrFail
import uk.matvey.slon.sql.getLongOrFail
import uk.matvey.slon.sql.getStringOrFail
import java.time.ZoneOffset.UTC

class MovieRepository(
    private val db: SuspendingConnection,
) {

    suspend fun getById(id: Long): Movie {
        return requireNotNull(findById(id)) {
            "Not found movie by id $id"
        }
    }

    suspend fun findById(id: Long): Movie? {
        return db.execute(
            "select * from vtornik.movies where id = ?",
            listOf(id)
        ).rows.singleOrNull()?.let { toMovie(it) }
    }

    suspend fun findAllByIds(ids: List<Long>): List<Movie> {
        return db.execute("select * from vtornik.movies where id = any(?)", listOf(ids))
            .rows.map { toMovie(it) }
    }

    suspend fun add(details: Movie.Details.Tmdb): Long {
        return db.execute(
            """
                |insert into vtornik.movies (id, title, runtime, year, details, created_at, updated_at)
                | values (?, ?, ?, ?, ?, now(), now())
                | returning id
                |""".trimMargin(),
            listOf(
                details.id,
                details.title,
                details.runtime,
                details.releaseDateOrNull()?.year,
                Json.encodeToString(Movie.Details(tmdb = details)),
            )
        ).rows.single().getLongOrFail("id")
    }

    private fun toMovie(data: RowData): Movie {
        val details = Json.decodeFromString<Movie.Details>(data.getStringOrFail("details"))
        return Movie(
            id = data.getLongOrFail("id"),
            title = data.getStringOrFail("title"),
            runtime = data.getIntOrFail("runtime"),
            year = details.tmdb?.releaseDateOrNull()?.year,
            details = details,
            createdAt = data.getDateOrFail("created_at").toInstant(UTC),
            updatedAt = data.getDateOrFail("updated_at").toInstant(UTC),
        )
    }
}