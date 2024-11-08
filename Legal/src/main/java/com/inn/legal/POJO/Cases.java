package com.inn.legal.POJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
//
//@NamedQuery(name = "Case.findByClientId",
//        query = "SELECT c FROM Case c WHERE c.client.clientID = :clientID")
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "case_table") // Table name should be meaningful
public class Cases implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "caseID")
    private Integer caseID;  // Primary Key

    // Foreign Key to Client
    @ManyToOne(fetch = FetchType.LAZY)  // Many cases can belong to one client
    @JoinColumn(name = "clientID", referencedColumnName = "clientID", nullable = false)
    private Client client;

    // Foreign Key to Lawyer (Assuming Lawyer is a subclass of User or a separate entity)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyerid", referencedColumnName = "lawyerID", nullable = false)
    private Lawyer lawyer;

    @Column(name = "caseDescription", columnDefinition = "TEXT")
    private String caseDescription;  // Detailed description of the case

    @Column(name = "dateCreated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;  // Timestamp of case creation

    public Cases() {
        this.dateCreated = new Date();  // Initialize the creation date when the case is created
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CaseStatus status;  // Case status (Open/Closed)

    // Enum for the status of the case
    public enum CaseStatus {
        OPEN,
        CLOSED
    }
}
