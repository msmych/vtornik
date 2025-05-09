package uk.matvey.vtornik.person

import com.github.jasync.sql.db.SuspendingConnection
import uk.matvey.slon.sql.execute
import uk.matvey.vtornik.VtornikSql.MOVIES_PEOPLE
import uk.matvey.vtornik.movie.Movie

class MoviePersonRepository(
    private val db: SuspendingConnection,
) {
    suspend fun addMoviePerson(movieId: Long, personId: Long, role: Movie.Role) {
        db.execute(
            """
                |insert into $MOVIES_PEOPLE (movie_id, person_id, role, created_at)
                | values (?, ?, ?, now())
                |""".trimMargin(),
            listOf(movieId, personId, role)
        )
    }
}
