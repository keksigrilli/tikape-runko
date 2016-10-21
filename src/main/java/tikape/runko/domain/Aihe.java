/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.domain;

public class Aihe {
    private Integer id;
    private Integer alueid;
    private String kirjoittaja;
    private String kuvaus;
    private String nimi;
    private String viimeisin;
    private Integer viestit;
    private String luotu;

    public Aihe(Integer id, Integer alueid, String kirjoittaja, String nimi, String kuvaus, String luotu, String viimeisin, Integer viestit) {
        this.id = id;
        this.alueid = alueid;
        this.kirjoittaja = kirjoittaja;
        this.kuvaus = kuvaus;
        this.nimi = nimi;
        this.viimeisin = viimeisin;
        this.viestit = viestit;
        this.luotu = luotu;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAlueid() {
        return alueid;
    }

    public void setAlueid(Integer alueid) {
        this.alueid = alueid;
    }

    public String getKirjoittaja() {
        return kirjoittaja;
    }

    public void setKirjoittaja(String kirjoittaja) {
        this.kirjoittaja = kirjoittaja;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getViimeisin() {
        return viimeisin;
    }

    public void setViimeisin(String viimeisin) {
        this.viimeisin = viimeisin;
    }

    public Integer getViestit() {
        return viestit;
    }

    public void setViestit(Integer viestit) {
        this.viestit = viestit;
    }

    public String getLuotu() {
        return luotu;
    }

    public void setLuotu(String luotu) {
        this.luotu = luotu;
    }

   

    
    
    
    
}
