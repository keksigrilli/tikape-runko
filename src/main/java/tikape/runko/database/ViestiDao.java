/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import tikape.runko.domain.Viesti;

public class ViestiDao implements Dao<Viesti, Integer> {

    private Database database;
    private int lkm;

    public ViestiDao(Database database) {
        this.database = database;
        this.lkm = 10;
        
    }

    @Override
    public Viesti findOne(Integer key) throws SQLException {
        return null;
    }

    @Override
    public List<Viesti> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<Viesti> getViestit(Integer ide, Integer page) throws SQLException {
        Connection conn = database.getConnection();
        //käyttötapaus 3/3
        PreparedStatement stmt = conn.prepareStatement("SELECT viesti.id AS ID, viesti.kirjoittaja AS AUTHOR,"
                                                    + " viesti.aiheid AS AIHEID, viesti.moneskoviesti AS MONESKO,"
                                                    + " viesti.sisalto AS SISALTO, viesti.aika AS AIKA FROM Viesti"
                                                    + " WHERE Viesti.aiheid = (SELECT aihe.id FROM aihe WHERE aihe.id = ?)"
                                                    + " ORDER BY moneskoViesti ASC LIMIT ? OFFSET ?");
        stmt.setObject(1, ide);
        stmt.setObject(2, lkm);
        stmt.setObject(3, lkm * (page -1));
        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("ID");
            String kirjoittaja = rs.getString("AUTHOR");
            String sisalto = rs.getString("SISALTO");
            Integer aiheid = rs.getInt("AIHEID");
            String aika = rs.getString("AIKA");
            Integer monesko = rs.getInt("MONESKO");
            System.out.println(kirjoittaja +", " +sisalto +", " +aika +", " +monesko);
            
            viestit.add(new Viesti(id, aiheid, kirjoittaja, sisalto, aika, monesko));
        }

        rs.close();
        stmt.close();
        conn.close();

        return viestit;
    } 

    public void setLkm(int lkm) {
        this.lkm = lkm;
    }
    
    // Palauttaa pienimmän määrän sivuja, joita tarvitaan näyttämään kaikki aiheen viestit.
    public Integer maxPage(Integer aiheid) throws Exception{
        Connection conn = database.getConnection();
        PreparedStatement viestit = conn.prepareStatement("SELECT COUNT(viesti.id) AS MAARA FROM viesti WHERE viesti.aiheid = ?");
        viestit.setObject(1, aiheid);
        ResultSet rs = viestit.executeQuery();
        boolean onko = rs.next();
        if(!onko) {
            return 0;
        }
        int maara = rs.getInt("MAARA");
        rs.close();
        viestit.close();
        conn.close();
        
        return (maara/lkm) +1;
    }
 
    // Lisää viestin tietokantaan
    public void save(String kirjoittaja, String viesti, Integer aiheid) throws SQLException {
        if(kirjoittaja.isEmpty()) { // Jos nimi on jätetty tyhjäksi, olkoon hän anonyymi.
            kirjoittaja = "Anonyymi";
        }
        Integer monesko = 1;
        Connection conn = database.getConnection();
        PreparedStatement mones = conn.prepareStatement("SELECT viesti.moneskoviesti AS mones FROM Viesti WHERE viesti.aiheid = ?"
                                                    + " ORDER BY viesti.moneskoviesti DESC LIMIT 1");
        mones.setObject(1, aiheid);
        ResultSet rs = mones.executeQuery();
        boolean eka = rs.next();
        if (eka) {
            monesko += rs.getInt("mones");
        }
        mones = conn.prepareStatement("SELECT DATETIME('now', 'localtime')");
        rs = mones.executeQuery();
        String pvm = rs.getString(1);         
        mones.close();
        rs.close();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Viesti (kirjoittaja, sisalto, aiheid, aika, moneskoviesti)"
                                                    + " VALUES(?, ?, ?, ?, ?)");
        stmt.setObject(1, kirjoittaja);
        stmt.setObject(2, viesti);
        stmt.setObject(3, aiheid);
        stmt.setObject(4, pvm);
        stmt.setObject(5, monesko);

        
        stmt.execute();
        stmt.close();
        conn.close();
    }

}
