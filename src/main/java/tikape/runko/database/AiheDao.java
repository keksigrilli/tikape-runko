/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Aihe;

public class AiheDao implements Dao<Aihe, Integer> {
    
    private Database database;

    public AiheDao(Database database) {
        this.database = database;
    }

    @Override
    public Aihe findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM aihe WHERE aihe.id = " +key);
        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }
        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");
        String kuvaus = rs.getString("sisalto");
            
        Aihe aihe = new Aihe(id, nimi, kuvaus);


        rs.close();
        stmt.close();
        connection.close();

        return aihe;
    }

    @Override
    public List<Aihe> findAll() throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Aihe");
        
        ResultSet rs = stmt.executeQuery();
        List<Aihe> aiheet = new ArrayList<>();
        while(rs.next()) {
            Integer id = rs.getInt("id");
            Integer alueid = rs.getInt("alueid");
            String nimi = rs.getString("nimi");
            String sisalto = rs.getString("sisalto");
            String aika = rs.getString("aika");
            String kirjoittaja = rs.getString("kirjoittaja");
            
            aiheet.add(new Aihe(id, alueid, kirjoittaja, sisalto, nimi, aika, null));

        }
        
        return aiheet;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<Aihe> getAiheet(Integer id) throws SQLException {
        Connection connection = database.getConnection();
        //käyttötapaus 2/3 kysely
        PreparedStatement kysy = connection.prepareStatement("SELECT Alue.nimi AS alue FROM Alue WHERE Alue.id = ?");
        kysy.setObject(1, id);
        ResultSet alueNimi = kysy.executeQuery();
        String alue = alueNimi.getString("alue");
        PreparedStatement stmt = connection.prepareStatement("SELECT Aihe.id AS ID, Aihe.nimi AS 'Alue: " +alue +"', COUNT(viesti.id) AS 'Viestejä', viesti.aika AS 'Viimeisin viesti' FROM Alue LEFT JOIN Aihe ON Aihe.alueid = Alue.id LEFT JOIN viesti ON viesti.aiheid = aihe.id WHERE alue.nimi = '" +alue +"' GROUP BY Aihe.id ORDER BY COUNT(viesti.id) DESC");

        ResultSet rs = stmt.executeQuery();
        List<Aihe> aiheet = new ArrayList<>();
        while (rs.next()) {
            Integer aiheid = rs.getInt("ID");
            String nimi= rs.getString("Alue: " +alue);
            Integer viestit = rs.getInt("Viestejä");
            String viimeisin = rs.getString("Viimeisin viesti");

            aiheet.add(new Aihe(aiheid, nimi, viestit, viimeisin));
        }

        rs.close();
        stmt.close();
        connection.close();

        return aiheet;
    }
    
}
