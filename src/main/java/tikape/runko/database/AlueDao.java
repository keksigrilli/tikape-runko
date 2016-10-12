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
import tikape.runko.domain.Alue;

public class AlueDao implements Dao<Alue, Integer>{
    private Database database;

    public AlueDao(Database database) {
        this.database = database;
    }

    @Override
    public Alue findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM alue WHERE alue.id = " +key);
        ResultSet rs = stmt.executeQuery();
        if(!rs.next()) {
            return null;
        }
        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");
        String kuvaus = rs.getString("kuvaus");
            
        Alue alue = new Alue(id, nimi, kuvaus, null, null);


        rs.close();
        stmt.close();
        connection.close();

        return alue;
        
    }

    @Override
    public List<Alue> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM alue");
        ResultSet rs = stmt.executeQuery();
        List<Alue> alueet = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");
            String kuvaus = rs.getString("kuvaus");

            alueet.add(new Alue(id, nimi, kuvaus, null, null));
        }

        rs.close();
        stmt.close();
        connection.close();

        return alueet;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<Alue> getAlueet() throws SQLException {
        Connection connection = database.getConnection();
        //käyttötapaus 1/3 kysely
        PreparedStatement stmt =
                connection.prepareStatement("SELECT Alue.id AS ID, Alue.nimi AS ALUE, Alue.kuvaus AS KUVAUS,"
                                        + " COUNT(Viesti.id) AS VIESTEJA, viesti.aika AS UUSIN"
                                        + " FROM Alue LEFT JOIN Aihe ON Alue.id = Aihe.Alueid"
                                        + " LEFT JOIN Viesti ON Aihe.id = Viesti.aiheid GROUP BY Alue.id ORDER BY COUNT(viesti.id) DESC");

        ResultSet rs = stmt.executeQuery();
        List<Alue> alueet = new ArrayList<>();
        while (rs.next()) {
            String nimi = rs.getString("ALUE");
            Integer viestit = rs.getInt("VIESTEJA");
            String aika = rs.getString("UUSIN");
            Integer id = rs.getInt("ID");
            String kuvaus = rs.getString("KUVAUS");

            alueet.add(new Alue(id, nimi, kuvaus, viestit, aika));
            
        }

        rs.close();
        stmt.close();
        connection.close();

        return alueet;
    }

    public void save(String nimi, String kuvaus) throws Exception{
        Connection conn = database.getConnection();

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Alue (nimi, kuvaus) VALUES(?, ?)");
        stmt.setObject(1, nimi);
        stmt.setObject(2, kuvaus);
        
        stmt.execute();
        stmt.close();
        conn.close();
    }
}
