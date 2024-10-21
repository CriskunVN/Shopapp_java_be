package com.springboottayjv.demolearntayjv.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboottayjv.demolearntayjv.dto.request.AddressDTO;
import com.springboottayjv.demolearntayjv.util.Gender;
import com.springboottayjv.demolearntayjv.util.UserStatus;
import com.springboottayjv.demolearntayjv.util.UserType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user")
public class UserEntity extends AbstractEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private UserType type;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private UserStatus status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private Set<AddressEntity> addresses = new HashSet<>();

    public void saveAddress(AddressEntity address) {
        if (address != null) {
            if (addresses == null) {
                addresses = new HashSet<>();
            }
            addresses.add(address);
            address.setUser(this); // save user_id
        }
    }

    // https://stackoverflow.com/questions/56899986/why-infinite-loop-hibernate-when-load-data
    @JsonIgnore // Stop infinite loop
    public Set<AddressEntity> getAddresses() {
        return addresses;
    }
}
