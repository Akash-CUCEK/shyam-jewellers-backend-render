package com.shyam.repository;

import com.shyam.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  Optional<Category> findByName(String name);

  boolean existsByName(String name);

  @Query(
      """
    SELECT CASE
        WHEN COUNT(c) < 7 THEN true
        ELSE false
    END
    FROM Category c
    WHERE c.showOnHome = true
""")
  boolean canEnableShowOnHome();
}
