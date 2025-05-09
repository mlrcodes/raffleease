package com.raffleease.raffleease.Domains.Associations.Model;

import com.raffleease.raffleease.Domains.Users.Model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "Association_memberships")
public class AssociationMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "association_id", nullable = false)
    private Association association;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssociationRole role;
}