package com.b1.testing.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "dbingest")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DbIngest {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "judul")
    private String judul;

    @Column(name = "tim_liputan")
    private String timLiputan;

    @Column(name = "reporter")
    private String reporter;

    @Column(name = "lok_liputan")
    private String lokLiputan;

    @Column(name = "deskripsi")
    private String deskripsi;

    @Column(name = "no_tape")
    private Integer noTape;

    @Column(name = "files")
    private String files;
}
