package com.b1.testing.entity;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "kontri")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Kontri extends DateAudit{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_kontri")
    private Integer idKontri;

    @Column(name = "judul")
    private String judul;

    @Column(name = "tim_liputan")
    private String timLiputan;

    @Column(name = "caption")
    private String caption;
    
    @Column(name = "reporter")
    private String reporter;

    @Column(name = "lok_liputan")
    private String lokLiputan;

    @Column(name = "deskripsi", columnDefinition = "TEXT")
    private String deskripsi;

    @Column(name = "no_tape")
    private Integer noTape;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_kontri", referencedColumnName = "id_kontri")
    private List<Video> listVideo;
}
