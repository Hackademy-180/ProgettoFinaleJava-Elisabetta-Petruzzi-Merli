package it.aulab.progetto_finale_java.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import it.aulab.progetto_finale_java.models.Article;
import it.aulab.progetto_finale_java.models.Category;
import it.aulab.progetto_finale_java.models.User;

public interface ArticleRepository extends ListCrudRepository<Article, Long> {
    // 2 metodi derived query
    List<Article> findByCategory(Category category);
    List<Article> findByUser(User user);
    List<Article> findByIsAcceptedTrue();
    List<Article> findByIsAcceptedFalse();
    List<Article> findByIsAcceptedIsNull();

    @Query("SELECT a FROM Article a WHERE " + 
        "LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "LOWER(a.subtitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "LOWER(a.user.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " + 
        "LOWER(a.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))"
    )
    List<Article> search(@Param("searchTerm")String searchTerm);
}
