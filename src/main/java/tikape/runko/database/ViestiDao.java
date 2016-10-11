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

public class ViestiDao implements Dao<Viesti, Integer>{
    private Database database;

    public ViestiDao(Database database) {
        this.database = database;
    }

    @Override
    public Viesti findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("kirjoittaja");
        String sisalto = rs.getString("sisalto");
        Integer aiheid = rs.getInt("aiheid");
        String aika = rs.getString("aika");

        

        rs.close();
        stmt.close();
        connection.close();

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

    public List<Viesti> getViestit(Integer id) throws SQLException {
        Connection conn = database.getConnection();
        //käyttötapaus 3/3
        PreparedStatement stmt = conn.prepareStatement("SELECT Viesti.moneskoviesti AS monesko, Viesti.sisalto AS sisalto, Viesti.aika AS pvm FROM Viesti WHERE Viesti.aiheid = (SELECT aihe.id FROM aihe WHERE aihe.id = ?) ORDER BY moneskoViesti DESC");
        stmt.setObject(1, id);
        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            Integer monesko = rs.getInt("monesko");
            String sisalto = rs.getString("sisalto");
            String aika = rs.getString("pvm");
            
            viestit.add(new Viesti(sisalto, aika, monesko));
        }

        rs.close();
        stmt.close();
        conn.close();

        return viestit;
    }
    
    public void save(String nimi, String viesti, Integer aiheid) throws SQLException {
        Integer monesko = 1;
        Connection conn = database.getConnection();
        PreparedStatement mones = conn.prepareStatement("SELECT COUNT(viesti.id) AS mones FROM Viesti WHERE viesti.aiheid = ?");
        mones.setObject(1, aiheid);
        ResultSet rs = mones.executeQuery();
        boolean eka = rs.next();
        if(eka) {
            monesko = rs.getInt("mones");
        }
        mones.close();
        rs.close();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Viesti (kirjoittaja, sisalto, aiheid, aika, moneskoviesti) VALUES(?, ?, ?, '1997-12-10 14:44', ?)");
        stmt.setObject(1, nimi);
        stmt.setObject(2, viesti);
        stmt.setObject(3, aiheid);
        stmt.setObject(4, monesko);
        stmt.execute();
        stmt.close();
        conn.close();
    }
    
}
