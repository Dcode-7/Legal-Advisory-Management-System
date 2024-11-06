package com.inn.legal.POJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
@NamedQuery(name = "User.findByEmailIdAndRole",
        query = "SELECT u FROM User u WHERE u.email = :email AND u.role = :role")
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "role"}) // Composite unique constraint
})
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userID;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING) // Use Enum to represent roles
    private Role role;

//    Making the Role enum public enhances the flexibility and usability of your code by allowing other classes
//    to easily reference and use the defined roles.
    public enum Role {
        CLIENT, LAWYER
    }

    @Column(name = "status")
    private String status;

    // Establish a One-to-One relationship with Client
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientID", referencedColumnName = "clientID")
    private Client client;  // This is the object representing the related Client

}
