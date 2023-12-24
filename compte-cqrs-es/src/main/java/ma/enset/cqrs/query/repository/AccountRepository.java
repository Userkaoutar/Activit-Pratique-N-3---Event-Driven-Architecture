package ma.enset.cqrs.query.repository;

import ma.enset.cqrs.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Account,String> {
}
