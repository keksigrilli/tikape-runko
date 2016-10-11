/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.domain;

import java.sql.Date;
import java.sql.Time;

public class Aihe {
    private Integer id;
    private Integer alueid;
    private String kirjoittaja;
    private String sisalto;
    private String nimi;
    private String viimeisin;
    private Integer viestit;

    public Aihe(Integer id, Integer alueid, String kirjoittaja, String sisalto, String nimi, String viimeisin, Integer viestit) {
        this.id = id;
        this.alueid = alueid;
        this.kirjoittaja = kirjoittaja;
        this.sisalto = sisalto;
        this.nimi = nimi;
        this.viimeisin = viimeisin;
        this.viestit = viestit;
    }
    
    public Aihe(Integer id, String nimi, String kuvaus) {
        this(id, null, null, kuvaus, nimi, null, null);
    }

     public Aihe(Integer id, String nimi, Integer viestit, String viimeisin) {
        this(id, null, null, null, nimi, viimeisin, viestit);
    }


    public Aihe(Integer alueid, String kirjoittaja, String nimi, String sisalto, String viimeisin) {
        this(null, alueid, kirjoittaja, sisalto, nimi, viimeisin, null);
    }

    public Aihe(String nimi, Integer viestit, String viimeisin) {
        this(null, null, null, null, nimi, viimeisin, viestit);
        
    }
    public Integer getAlueid() {
        return alueid;
    }

    public Integer getId() {
        return id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getKirjoittaja() {
        return kirjoittaja;
    }

    public Integer getViestit() {
        return viestit;
    }

    public String getViimeisin() {
        return viimeisin;
    }

    public void setKirjoittaja(String kirjoittaja) {
        this.kirjoittaja = kirjoittaja;
    }

    public void setViestit(Integer viestit) {
        this.viestit = viestit;
    }

    public void setViimeisin(String viimeisin) {
        this.viimeisin = viimeisin;
    }

    public String getSisalto() {
        return sisalto;
    }



    public void setAlueid(Integer alueid) {
        this.alueid = alueid;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    

    public void setSisalto(String sisalto) {
        this.sisalto = sisalto;
    }

    
    
    
    
}
