package com.rlrio.voting.repository.specification;

import com.rlrio.voting.model.RestaurantEntity;
import com.rlrio.voting.model.VoteEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class RestaurantSpecification {
    public static Specification<RestaurantEntity> nameStartsWith(String nameStartsWith) {
        return (Root<RestaurantEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (nameStartsWith == null) {
                return cb.conjunction();
            }
            return cb.like(root.get("name"), nameStartsWith + "%");
        };
    }

    public static Specification<RestaurantEntity> hasMoreVotesThan(Long minVotes) {
        return (Root<RestaurantEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (minVotes == null || minVotes == 0) {
                return cb.conjunction();
            }
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<VoteEntity> subRoot = subquery.from(VoteEntity.class);
            subquery.select(cb.count(subRoot));
            Predicate datePredicate = cb.greaterThanOrEqualTo(subRoot.get("voteDateTime").as(LocalDateTime.class),
                    LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
            subquery.where(cb.equal(subRoot.get("restaurant"), root), datePredicate);

            return cb.ge(subquery, minVotes);
        };
    }
}
