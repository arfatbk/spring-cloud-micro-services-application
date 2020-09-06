package com.arfat.OAuthServer.modals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Arfat Bin Kileb
 * Created at 06-09-2020 02:06 PM
 */
@Entity
@Table(name = "permission")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "name")
    private String name;
}
