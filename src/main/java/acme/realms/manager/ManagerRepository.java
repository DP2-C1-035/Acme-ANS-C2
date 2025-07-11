
package acme.realms.manager;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;

public interface ManagerRepository extends AbstractRepository {

	@Query("SELECT m FROM Manager m WHERE m.identifierNumber = :identifierNumber")
	Manager findManagerByIndentifier(String identifierNumber);

}
