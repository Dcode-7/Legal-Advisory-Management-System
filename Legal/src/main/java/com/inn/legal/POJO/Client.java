package com.inn.legal.POJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;

@NamedQuery(name = "Client.findByEmail",
        query = "SELECT u FROM Client u WHERE u.email = :email")
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "client", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"}) // Unique constraint on email
})
public class Client implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer clientID;

    @Column(name = "name")
    private String name;

    @Column(name = "contactNo")
    private String contactNo;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "address")
    private String address;

    @Column(name = "email", nullable = false, unique = true)  // Email is mandatory and unique
    private String email;

    // One-to-One relationship with User
    @OneToOne(mappedBy = "client", fetch = FetchType.LAZY)  // "client" is the name of the field in User
    private User user;  // This is the back reference to the associated User
}
