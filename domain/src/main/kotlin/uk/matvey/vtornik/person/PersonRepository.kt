package uk.matvey.vtornik.person

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import uk.matvey.slon.sql.execute
import uk.matvey.slon.sql.getDateOrFail
import uk.matvey.slon.sql.getLongOrFail
import uk.matvey.slon.sql.getStringOrFail
import uk.matvey.vtornik.VtornikSql.MOVIES_PEOPLE
import uk.matvey.vtornik.VtornikSql.PEOPLE
import uk.matvey.vtornik.movie.Movie
import java.time.ZoneOffset.UTC

class PersonRepository(
    private val db: SuspendingConnection,
) {

    suspend fun findById(id: Long): Person? {
        return db.execute(
            "select * from $PEOPLE where id = ?",
            listOf(id)
        ).rows.singleOrNull()?.let { toPerson(it) }
    }

    suspend fun getById(id: Long): Person {
        return requireNotNull(findById(id)) {
            "Person with id $id was not found"
        }
    }

    suspend fun findAllPeopleByMovieId(
        movieId: Long,
        role: Movie.Role,
    ): List<Person> {
        return db.execute(
            """
            |select p.*
            | from $PEOPLE p
            | join $MOVIES_PEOPLE mp on p.id = mp.person_id
            | where mp.movie_id = ? and mp.role = ?
            |""".trimMargin(),
            listOf(movieId, role.name)
        ).rows.map { toPerson(it) }
    }

    suspend fun findAllPeopleByMoviesIds(
        moviesIds: List<Long>,
        role: Movie.Role,
    ): Map<Long, List<Person>> {
        return db.execute(
            """
            |select mp.movie_id, p.*
            | from $PEOPLE p
            | join $MOVIES_PEOPLE mp on p.id = mp.person_id
            | where mp.movie_id = any(?) and mp.role = ?
            |""".trimMargin(),
            listOf(moviesIds, role.name)
        ).rows.map { it.getLongOrFail("movie_id") to toPerson(it) }
            .groupBy { (k, _) -> k }
            .mapValues { (_, v) -> v.map { it.second } }
    }

    suspend fun ensurePerson(id: Long, name: String): Long {
        return findById(id)?.id ?: db.execute(
            """
            |insert into $PEOPLE (id, name, created_at, updated_at)
            | values (?, ?, now(), now())
            | on conflict do nothing
            | returning id
            |""".trimMargin(),
            listOf(
                id,
                name,
            )
        ).rows.single().getLongOrFail("id")
    }

    private fun toPerson(data: RowData): Person {
        return Person(
            id = data.getLongOrFail("id"),
            name = data.getStringOrFail("name"),
            createdAt = data.getDateOrFail("created_at").toInstant(UTC),
            updatedAt = data.getDateOrFail("updated_at").toInstant(UTC),
        )
    }
}
