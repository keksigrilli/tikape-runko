
package tikape.runko.domain;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Viesti {
    private Integer id;
    private String kirjoittaja;
    private String sisalto;
    private Integer aiheid;
    private String pvm;
    private Integer moneskoViesti;

    public Viesti(Integer id, String kirjoittaja, String sisalto, Integer aiheid, String pvm, Integer monesko) {
        this.id = id;
        this.kirjoittaja = kirjoittaja;
        this.sisalto = sisalto;
        this.aiheid = aiheid;
        this.pvm = pvm;
        this.moneskoViesti = monesko;
    }

    public Viesti(String sisalto, String pvm, Integer monesko) {
        this(null, null, sisalto, null, pvm, null);
    }
    public Viesti(String kirjoittaja, String sisalto, Integer aiheid, String pvm) {
        this(null, kirjoittaja, sisalto, aiheid, pvm, null);
    }

    public Integer getId() {
        return id;
    }

    public Integer getAiheid() {
        return aiheid;
    }

    public String getKirjoittaja() {
        return kirjoittaja;
    }

    public String getSisalto() {
        return sisalto;
    }

    public String getPvm() {
        return pvm;
    }

    public Integer getMoneskoViesti() {
        return moneskoViesti;
    }
    public void setMoneskoViesti(Integer moneskoViesti) {
        this.moneskoViesti = moneskoViesti;
    }

    public void setAiheid(Integer aiheid) {
        this.aiheid = aiheid;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setKirjoittaja(String kirjoittaja) {
        this.kirjoittaja = kirjoittaja;
    }

    public void setPvm(String pvm) {
        this.pvm = pvm;
    }

    public void setSisalto(String sisalto) {
        this.sisalto = sisalto;
    }

    
    
    
    
    
    
    
}
