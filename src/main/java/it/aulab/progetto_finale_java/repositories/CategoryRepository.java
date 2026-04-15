package it.aulab.progetto_finale_java.repositories;

import org.springframework.data.repository.ListCrudRepository;
import it.aulab.progetto_finale_java.models.Category;

public interface CategoryRepository extends ListCrudRepository<Category,Long>{

}
