package com.zespolowka.repository;

import java.time.LocalDate;
import java.util.Collection;

import com.zespolowka.entity.createtest.Test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
	Test findTestById(Long id);

	Collection<Test> findByEndDateBefore(LocalDate date);

	Collection<Test> findByEndDateAfter(LocalDate date);
}
