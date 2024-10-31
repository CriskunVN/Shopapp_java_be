package com.springboottayjv.demolearntayjv.service;

import com.springboottayjv.demolearntayjv.dto.request.UserDTO;
import com.springboottayjv.demolearntayjv.dto.response.PageResponse;
import com.springboottayjv.demolearntayjv.dto.response.UserDetailResponse;
import com.springboottayjv.demolearntayjv.model.UserEntity;
import com.springboottayjv.demolearntayjv.util.Gender;
import com.springboottayjv.demolearntayjv.util.UserStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface UserService{
    long saveUser(UserDTO request);

    void updateUser(long userId, UserDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsers(int pageNo, int pageSize,String sortBy);

    PageResponse<?> getAllUserWithSortByMultupleColumns(int pageNo, int pageSize,String... sorts);

    PageResponse<?> getAllUserWithSortByColumnAndSearch(int pageNo, int pageSize,String search,String sortBy);

    PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String sortBy,String address , String... search);

    PageResponse<?> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] address);

    List<UserEntity> getUserByFirstNameLike(String name);

    List<UserEntity> getListUserByCity(String city);

    List<UserEntity> getListUsersByCreatedAtBetween(Date start, Date end);

    List<UserEntity> getListUsersByFirstNameAndLastName(String firstName, String lastName);

    List<UserEntity> getListUsersByEmail(String email);

    List<UserEntity> getListUsersByAgeLessThan(int age);

    List<UserEntity> getListUsersByAgeGreaterThan(int age);

    List<UserEntity> getUsersByCreatedAtAfter(Date date);

    List<UserEntity> getUsersByCreatedAtBefore(Date date);
}
