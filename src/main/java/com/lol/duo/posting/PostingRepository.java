package com.lol.duo.posting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingRepository extends JpaRepository<Posting, Integer> {
    Page<Posting> findAll(Pageable pageable);
    Page<Posting> findAll(Specification<Posting> spec, Pageable pageable);

}
