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
@Table(name = "video")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Video {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_video")
    private Integer idVideo;

    @Column(name = "id_ingest")
    private Integer idIngest;

    @Column(name = "id_kontri")  
    private Integer idKontri;

    @Column(name = "ip_location")
    private String ipLocation;

    @Column(name = "foldername")
    private String foldername;

    @Column(name = "filename")
    private String filename;

    @Column(name = "caption", columnDefinition = "TEXT")
    private String caption;

    @Column(name = "transcode_extension")
    private String transcodeExtension;

    @Column(name = "original_extension")
    private String originalExtension;
}
