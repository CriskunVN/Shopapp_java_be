package com.springboottayjv.demolearntayjv.service.impl;

import com.springboottayjv.demolearntayjv.dto.request.AddressDTO;
import com.springboottayjv.demolearntayjv.dto.request.UserDTO;
import com.springboottayjv.demolearntayjv.dto.response.PageResponse;
import com.springboottayjv.demolearntayjv.dto.response.UserDetailResponse;
import com.springboottayjv.demolearntayjv.exception.ResourceNotFoundException;
import com.springboottayjv.demolearntayjv.model.AddressEntity;
import com.springboottayjv.demolearntayjv.model.UserEntity;
import com.springboottayjv.demolearntayjv.repository.SearchRepository;
import com.springboottayjv.demolearntayjv.repository.UserRepository;
import com.springboottayjv.demolearntayjv.repository.specification.UserSpec;
import com.springboottayjv.demolearntayjv.repository.specification.UserSpecificationsBuilder;
import com.springboottayjv.demolearntayjv.service.UserService;
import com.springboottayjv.demolearntayjv.util.Gender;
import com.springboottayjv.demolearntayjv.util.UserStatus;
import com.springboottayjv.demolearntayjv.util.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final SearchRepository searchRepository;

    @Override
    public long saveUser(UserDTO request) {
        UserEntity user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(request.getStatus())
                .type(UserType.valueOf(request.getType().toUpperCase()))
                .build();
        request.getAddresses().forEach(a ->
                user.saveAddress(AddressEntity.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build()));

        userRepository.save(user);


        log.info("User has save!");
        return user.getId();
           }

        @Override
    public void updateUser(long userId, UserDTO request) {
        UserEntity user = getUserById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        if (!request.getEmail().equals(user.getEmail())) {
            // check email from database if not exist then allow update email otherwise throw exception
            user.setEmail(request.getEmail());
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus(request.getStatus());
        user.setType(UserType.valueOf(request.getType().toUpperCase()));
        user.setAddresses(convertToAddress(request.getAddresses()));
        userRepository.save(user);

        log.info("User updated successfully");
    }

    @Override
    public void changeStatus(long userId, UserStatus status) {
        UserEntity user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);
        log.info("status changed");
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDetailResponse getUser(long userId) {
        UserEntity user = getUserById(userId);
        log.info("User : {} {}", user.getId(), user.getUsername());
        return UserDetailResponse.builder()
                .id(userId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }

    private UserEntity getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public PageResponse<?> getAllUsers(int pageNo, int pageSize, String sortBy) {
        //check pageNo
        int pageNoNew = 0;
        if(pageNo > 0 ) {
            pageNoNew = pageNo - 1;
        }

        List<Sort.Order> sorts = new ArrayList<>();
        // xử lý chuỗi sortBy (abc:asc or abc:desc)
        if(StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()) {
                if(matcher.group(3).equalsIgnoreCase("asc")) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                }
                else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }
        Pageable pageable = PageRequest.of(pageNoNew,pageSize, Sort.by(sorts));
        Page<UserEntity> users = userRepository.findAll(pageable);

        List<UserDetailResponse> response = users.stream().map(user -> UserDetailResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .build())
                .toList() ;

        return PageResponse.builder()
                .page(pageNo)
                .size(pageSize)
                .totalPage(users.getTotalPages())
                .items(response)
                .build();

    }

    @Override
    public PageResponse<?> getAllUserWithSortByMultupleColumns(int pageNo, int pageSize, String... sorts) {
       //check pageNo
        int pageNoNew = 0;
        if(pageNo > 0 ) {
            pageNoNew = pageNo - 1;
        }


        List<Sort.Order> orders = new ArrayList<>();

        if(orders != null) {
            for(String sortBy : sorts) {
                log.info("sortBy: {}", sortBy);
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);
                if(matcher.find()) {
                    if(matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    }
                    else {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }

        Pageable pageable = PageRequest.of(pageNoNew,pageSize, Sort.by(orders));
        Page<UserEntity> users = userRepository.findAll(pageable);

        List<UserDetailResponse> response = users.stream().map(user -> UserDetailResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .build())
                .toList() ;

        return PageResponse.builder()
                .page(pageNo)
                .size(pageSize)
                .totalPage(users.getTotalPages())
                .items(response)
                .build();

    }

    @Override
    public PageResponse<?> getAllUserWithSortByColumnAndSearch(int pageNo, int pageSize, String search, String sortBy) {
        return searchRepository.getAllUserWithSortByColumnAndSearch(pageNo,pageSize,search,sortBy);

    }

    @Override
    public PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, String address ,String... search) {

        return searchRepository.advanceSearchUser(pageNo,pageSize,sortBy,address,search);
    }

    @Override
    public PageResponse<?> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] address) {


        if(user != null && address.length != 0) {
            // search in table user and address => join table
            return searchRepository.getUserJoinedAddress(pageable, user, address);
        }
        else if(user != null){
            // search in table user => don't need join with table address
            UserSpecificationsBuilder builder = new UserSpecificationsBuilder();

            for(String s: user) {
                Pattern pattern = Pattern.compile("(\\w+?)([:<>~!])(.*)(\\p{Punct}?)(.*)(\\p{Punct}?)");
                Matcher matcher = pattern.matcher(s);
                if(matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }
            Page<UserEntity> users = userRepository.findAll(Objects.requireNonNull(builder.build()), pageable);

            return convertToPageResponse(users,pageable);
        }


        return convertToPageResponse(userRepository.findAll(pageable), pageable);

    }


    public UserType convertToEntity(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setType(UserType.valueOf(userDTO.getType().toUpperCase())); // Chuyển chuỗi sang enum
        return userEntity.getType();
    }


    public UserStatus convertStatusToEntity(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setStatus(UserStatus.valueOf(userDTO.getStatus().toString())); // Chuyển chuỗi sang enum
        return userEntity.getStatus();
    }


    private Set<AddressEntity> convertToAddress(Set<AddressDTO> addresses) {
        Set<AddressEntity> result = new HashSet<>();
        addresses.forEach(a ->
                result.add(AddressEntity.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())
        );
        return result;
    }


    /**
     * Convert Page<User> to PageResponse
     *
     * @param users
     * @param pageable
     * @return
     */
    private PageResponse<?> convertToPageResponse(Page<UserEntity> users, Pageable pageable) {
        List<UserDetailResponse> response = users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()).toList();
        return PageResponse.builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPage(users.getTotalPages())
                .items(response)
                .build();
    }

}