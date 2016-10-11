/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.domain;

public class Alue {
    private Integer id;
    private String nimi;
    private String kuvaus;
    private Integer viestit;
    private String viimeisin;

    public Alue(Integer id, String nimi, String kuvaus, Integer viestit, String viimeisin) {
        this.id = id;
        this.nimi = nimi;
        this.kuvaus = kuvaus;
        this.viestit = viestit;
        this.viimeisin = viimeisin;
    }

    public Alue(String nimi, String kuvaus) {
        this(null, nimi, kuvaus, null, null);
    }
    
    public Alue(Integer id, String nimi, Integer viestit, String viimeisin) {
        this(id, nimi, null, viestit, viimeisin);
    }

    public Integer getId() {
        return id;
    }

    public Integer getViestit() {
        return viestit;
    }

    public String getViimeisin() {
        return viimeisin;
    }

    public void setViestit(Integer viestit) {
        this.viestit = viestit;
    }

    public void setViimeisin(String viimeisin) {
        this.viimeisin = viimeisin;
    }
    

    public String getKuvaus() {
        return kuvaus;
    }

    public String getNimi() {
        return nimi;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
    
    
    
    
}
