package uk.matvey.vtornik.person

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.slon.random.randomWord
import uk.matvey.vtornik.DomainTestSetup
import kotlin.random.Random

class PersonRepositoryTest : DomainTestSetup() {

    @Test
    fun `should add person if not exists`() = runTest {
        // given
        val repository = PersonRepository(db)
        val name = randomWord()

        // when
        val id = repository.ensurePerson(Random.nextLong(), name)

        // then
        val person = repository.getById(id)
        assertThat(person.name).isEqualTo(name)
    }

    @Test
    fun `should return existing id if already exists`() = runTest {
        // given
        val repository = PersonRepository(db)
        val name = randomWord()
        val existingId = repository.ensurePerson(Random.nextLong(), name)

        // when
        val id = repository.ensurePerson(existingId, randomWord())

        // then
        assertThat(id).isEqualTo(existingId)
    }
}