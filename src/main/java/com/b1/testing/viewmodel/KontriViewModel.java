package com.b1.testing.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class KontriViewModel {
    private String judul;
    private String reporter;
    private String lokasiLiputan;
    private String deskripsi;
}
