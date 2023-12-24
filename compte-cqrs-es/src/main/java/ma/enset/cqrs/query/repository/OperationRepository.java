package ma.enset.cqrs.query.repository;

import ma.enset.cqrs.query.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository  extends JpaRepository<Operation,Long> {

}
