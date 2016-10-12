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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public Integer getViestit() {
        return viestit;
    }

    public void setViestit(Integer viestit) {
        this.viestit = viestit;
    }

    public String getViimeisin() {
        return viimeisin;
    }

    public void setViimeisin(String viimeisin) {
        this.viimeisin = viimeisin;
    }
    


    
}
