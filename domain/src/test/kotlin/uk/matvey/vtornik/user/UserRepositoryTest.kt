package uk.matvey.vtornik.user

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.vtornik.DomainTestSetup

class UserRepositoryTest : DomainTestSetup() {

    @Test
    fun `should create user from github details if not exists`() = runTest {
        // given
        val userRepository = UserRepository(db)

        // when
        val id = userRepository.createUserIfNotExists(1234, "user1", "Name")

        // then
        val user = userRepository.getById(id)
        assertThat(user.id).isEqualTo(id)
        assertThat(user.username).isEqualTo("user1")
        assertThat(user.details.github?.id).isEqualTo(1234)
        assertThat(user.details.github?.login).isEqualTo("user1")
        assertThat(user.details.github?.name).isEqualTo("Name")
        assertThat(user.createdAt).isEqualTo(user.updatedAt)
    }

    @Test
    fun `should skip if github id already exists`() = runTest {
        // given
        val userRepository = UserRepository(db)
        val existingId = userRepository.createUserIfNotExists(9876, "user2", "Name")

        // when
        val newId = userRepository.createUserIfNotExists(9876, "user3", "Name 2")

        // then
        assertThat(newId).isEqualTo(existingId)
    }
}