
package tikape.runko.domain;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Viesti {
    private Integer id;
    private Integer aiheid;
    private String kirjoittaja;
    private String sisalto;
    private String pvm;
    private Integer moneskoViesti;

    public Viesti(Integer id, Integer aiheid, String kirjoittaja, String sisalto, String pvm, Integer moneskoViesti) {
        this.id = id;
        this.aiheid = aiheid;
        this.kirjoittaja = kirjoittaja;
        this.sisalto = sisalto;
        this.pvm = pvm;
        this.moneskoViesti = moneskoViesti;
    }

    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKirjoittaja() {
        return kirjoittaja;
    }

    public void setKirjoittaja(String kirjoittaja) {
        this.kirjoittaja = kirjoittaja;
    }

    public String getSisalto() {
        return sisalto;
    }

    public void setSisalto(String sisalto) {
        this.sisalto = sisalto;
    }

    public Integer getAiheid() {
        return aiheid;
    }

    public void setAiheid(Integer aiheid) {
        this.aiheid = aiheid;
    }

    public String getPvm() {
        return pvm;
    }

    public void setPvm(String pvm) {
        this.pvm = pvm;
    }

    public Integer getMoneskoViesti() {
        return moneskoViesti;
    }

    public void setMoneskoViesti(Integer moneskoViesti) {
        this.moneskoViesti = moneskoViesti;
    }

 
}
