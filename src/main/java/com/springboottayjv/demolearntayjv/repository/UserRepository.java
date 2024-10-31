package com.springboottayjv.demolearntayjv.repository;

import com.springboottayjv.demolearntayjv.model.UserEntity;
import com.springboottayjv.demolearntayjv.util.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Collection;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> , JpaSpecificationExecutor<UserEntity> {

    @Query(value = "select u from UserEntity u inner join AddressEntity a on u.id = a.user.id where a.city = ?1")
    List<UserEntity> getListUserByCity(String city);

    List<UserEntity> findByGender(Gender gender);

    // -- distinct
    @Query(value = "select distinct u from UserEntity u WHERE u.firstName= ?1 AND u.lastName= ?2")
    List<UserEntity> getListUserDistinctByFirstNameAndLastName(String firstName, String lastName);

    // -- Single Field
    //@Query("SELECT * FROM User u WHERE u.email= ?1");
    List<UserEntity> findByEmail(String email);

//    // -- OR
//    //@Query("SELECT * FROM User u WHERE u.firstName= ?1 OR u.lastName= ?2");
//    List<UserEntity> findByFirstNameOrLastName(String lastName);

    // -- Equal, IS
    //@Query("select * from User u where u.fisrtName = ?1")
    List<UserEntity> findByFirstNameIs(String firstName);
    List<UserEntity> findByFirstNameEquals(String lastName);
    List<UserEntity> findByFirstName(String email);

    // -- Between
    //@Query("select * from User u where u.create_At between ?1 and ?2")
    List<UserEntity> findByCreatedAtBetween(Date start, Date end);

    // -- LessThan
    //@Query("select * from User u where u.age < ?1")
    List<UserEntity> findByAgeLessThan(int age);

    //@Query("select * from User u where u.age <= ?1")
    List<UserEntity> findByAgeLessThanEqual(int age);

    // -- Greater Than
    //@Query("select * from User u where u.age > ?1")
    List<UserEntity> findByAgeGreaterThan(int age);

    //@Query("select * from User u where u.age >= ?1")
    List<UserEntity> findByAgeGreaterThanEqual(int age);

    // -- Before and After
    //@Query("select * from User u where u.create_At < ?1")
    List<UserEntity> findByCreatedAtBefore(Date date);

    //@Query("select * from User u where u.create_At > ?1")
    List<UserEntity> findByCreatedAtAfter(Date date);

    // -- NotNull , Null
    //@Query("select * from User u where u.age is null")
    List<UserEntity> findByAgeIsNull();

    //@Query("select * from User u where u.age is not null")
    List<UserEntity> findByAgeIsNotNull();

    // -- Like
    //@Query("select * from User u where u.firstName like % ?1 %")
    List<UserEntity> findByFirstNameLike(String firstName);
    List<UserEntity> findByLastNameLike(String lastName);

    // -- NotLike
    //Query("select * from User u where u.firstName not like % ?1 %")
    List<UserEntity> findByFirstNameNotLike(String firstName);

    // -- StartingWith
    //@Query("select * from User u where u.firstName like ?1%")
    List<UserEntity> findByFirstNameStartingWith(String firstName);

    // -- EndingWith
    //@Query("select * from User u where u.firstName like %?1")
    List<UserEntity> findByFirstNameEndingWith(String firstName);

    // -- Containing
    //@Query("select * from User u where u.firstName like % ?1 %")
    List<UserEntity> findByFirstNameContaining(String name);

    // -- Not
    //@Query("select * from User u where u.firstName <> ?1")
    List<UserEntity> findByFirstNameNotContaining(String name);

    // -- In
    //@Query("select * from User u where u.age in (18,25,30...)")
    List<UserEntity> findByAgeIn(Collection<Integer> ages);

    // -- Not In
    //@Query("select * from User u where u.age not in (18,25,30...)")
    List<UserEntity> findByAgeNotIn(Collection<Integer> ages);

    // -- True/false
    //@Query("select * from User u where u.activeted=true")
    List<UserEntity> findByActiveTrue();
    //@Query("select * from User u where u.activeted=false")
    List<UserEntity> findByActiveFalse();

    // -- IgnoreCase
    //@Query("select * from User u where LOWER(u.firstName) = LOWER(?1) ")
    List<UserEntity> findByFirstNameIgnoreCase(String firstName);


    // -- OrderBy

    List<UserEntity> findByFirstNameOrderByCreatedAtDesc(String firstName);

    // -- All
    List<UserEntity> findByFirstNameAndLastNameAllIgnoreCase(String firstName,String lastName);

}
