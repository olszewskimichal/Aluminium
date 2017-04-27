package com.zespolowka.repository;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.zespolowka.entity.createtest.Test;
import com.zespolowka.entity.solution.test.SolutionStatus;
import com.zespolowka.entity.solution.test.SolutionTest;
import com.zespolowka.entity.user.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SolutionTestRepository extends JpaRepository<SolutionTest, Long> {
	@Transactional
	SolutionTest findSolutionTestById(Long id);

	Integer countSolutionTestsByUserAndTestAndSolutionStatus(User user, Test test, SolutionStatus solutionStatus);

	Integer countSolutionTestsByTestAndSolutionStatus(Test test, SolutionStatus solutionStatus);

	Collection<SolutionTest> findSolutionTestsByUserAndSolutionStatus(User user, SolutionStatus solutionStatus);

	Collection<SolutionTest> findSolutionTestsByTestAndSolutionStatusOrderByPointsDesc(Test test, SolutionStatus solutionStatus);

	Optional<SolutionTest> findSolutionTestByTestAndUserAndSolutionStatus(Test test, User user, SolutionStatus solutionStatus);

	@Query(value = "select s " + "from SolutionTest s where s.attempt =" + "(" + "select min(f.attempt) " + "from SolutionTest f " + "where (f.user=:user and f.test=s.test and f.solutionStatus=:solutionStatus) " + "and f.points = " + "(" + "select max(g.points) " + "from SolutionTest g " + "where (g.user=:user and g.test=f.test and g.solutionStatus=:solutionStatus) " + "group by g.test" + ") group by f.test" + ") and s.user=:user and s.solutionStatus=:solutionStatus")
	List<SolutionTest> getSolutionsWithTheBestResult(User user, SolutionStatus solutionStatus);

	@Transactional
	@Modifying
	void deleteSolutionTestsByUser(User user);

	@Transactional
	@Modifying
	void deleteSolutionTestsByTest(Test test);
}
