package com.shyam.repository;

import com.shyam.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByTagIdInAndActiveTrue(List<Long> tagIds);
}