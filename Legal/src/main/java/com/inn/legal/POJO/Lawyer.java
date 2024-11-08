package com.inn.legal.POJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;

//@NamedQuery(name = "Lawyer.findById",
//        query = "SELECT l FROM Lawyer l WHERE l.lawyerID= :lawyerID") // NamedQuery to fetch all lawyers
@NamedQuery(name = "Lawyer.findAll",
        query = "SELECT l FROM Lawyer l") // NamedQuery to fetch all lawyers
@NamedQuery(name = "Lawyer.findBySpecialization",
        query = "SELECT l FROM Lawyer l WHERE l.specialization= :specialization") // NamedQuery to fetch all lawyers

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "lawyer")
public class Lawyer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lawyerid")
    private Integer lawyerID;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contactNo", nullable = false)
    private String contactNo;

    @Column(name = "specialization", nullable = false)
    private String specialization;

    @Column(name = "fees", nullable = false)
    private Integer fees;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    // One-to-Many relationship with Client (Optional, if needed)
    // You can add relationships if required, e.g., a lawyer could have multiple cases associated, etc.
    // @OneToMany(mappedBy = "lawyer", fetch = FetchType.LAZY)
    // private List<Client> clients;

}
