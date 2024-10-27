package com.springboottayjv.demolearntayjv.repository.specification;

import com.springboottayjv.demolearntayjv.model.UserEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static com.springboottayjv.demolearntayjv.repository.specification.SearchOperation.*;
import static com.springboottayjv.demolearntayjv.repository.specification.SearchOperation.STARTS_WITH;

public final class UserSpecificationsBuilder {

    public final List<SpecSearchCriteria> specSearchCriteria;


    public UserSpecificationsBuilder() {
        this.specSearchCriteria = new ArrayList<>();
    }

    public UserSpecificationsBuilder with (String key, String operation, Object value, String prefix , String suffix) {

        return with( null,  key,  operation,  value,  prefix ,  suffix);
    }

    public UserSpecificationsBuilder with (String orPredicate, String key, String operation, Object value, String prefix , String suffix) {
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (searchOperation != null) {
            if (searchOperation == SearchOperation.EQUALITY) {
                boolean startWithAsterisk = prefix != null && prefix.contains(ZERO_OR_MORE_REGEX);
                boolean endWithAsterisk = suffix != null && suffix.contains(ZERO_OR_MORE_REGEX);

                if (startWithAsterisk && endWithAsterisk) {
                    searchOperation = CONTAINS;
                } else if (startWithAsterisk) {
                    searchOperation = ENDS_WITH;
                } else if (endWithAsterisk) {
                    searchOperation = STARTS_WITH;
                }
            }
            specSearchCriteria.add(new SpecSearchCriteria(orPredicate, key, searchOperation, value));
        }
        return this;
    }

    public Specification<UserEntity> build() {
        if(specSearchCriteria.isEmpty()) return null;

        Specification<UserEntity> result = new UserSpecification(specSearchCriteria.get(0));

        for (int i = 0; i < specSearchCriteria.size(); i++) {

            result = specSearchCriteria.get(i).getOrPredicate()
                    ? Specification.where(result).or(new UserSpecification(specSearchCriteria.get(i)))
                    : Specification.where(result).and(new UserSpecification(specSearchCriteria.get(i)));

        }

        return result;
    }
}
