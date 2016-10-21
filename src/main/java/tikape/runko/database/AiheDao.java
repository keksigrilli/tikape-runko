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
    private int lkm;

    public AiheDao(Database database) {
        this.database = database;
        this.lkm = 10;
    }

    @Override
    public Aihe findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM aihe WHERE aihe.id = " + key);
        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }
        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");
        String kuvaus = rs.getString("kuvaus");
        Integer alueid = rs.getInt("alueid");
        String kirjoittaja = rs.getString("kirjoittaja");
        String luotu = rs.getString("luotu");

        Aihe aihe = new Aihe(id, alueid, kirjoittaja, nimi, kuvaus, luotu, null, null);

        rs.close();
        stmt.close();
        connection.close();

        return aihe;
    }

    @Override
    public List<Aihe> findAll() throws SQLException {
        return null;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setLkm(int lkm) {
        this.lkm = lkm;
    }

    // Käyttötapaus 2/3
    public List<Aihe> getAiheet(Integer id, Integer page) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement kysy = connection.prepareStatement("SELECT Alue.nimi AS alue FROM Alue WHERE Alue.id = ?");
        kysy.setObject(1, id);
        ResultSet alueNimi = kysy.executeQuery();
        String alue = alueNimi.getString("alue");
        PreparedStatement stmt = connection.prepareStatement("SELECT Aihe.id AS ID, Aihe.alueid AS ALUE, Aihe.nimi AS NIMI,"
                + " Aihe.kirjoittaja AS KIRJOITTAJA, Aihe.luotu AS LUOTU, Aihe.kuvaus AS KUVAUS,"
                + " COUNT(viesti.id) AS VIESTEJA, viesti.aika AS 'VIIMEISIN'"
                + " FROM Alue LEFT JOIN Aihe ON Aihe.alueid = Alue.id LEFT JOIN viesti ON viesti.aiheid = aihe.id"
                + " WHERE alue.nimi = '" + alue + "' GROUP BY Aihe.id ORDER BY VIIMEISIN DESC LIMIT ? OFFSET ?");
        stmt.setObject(1, lkm);
        stmt.setObject(2, lkm * (page -1));

        ResultSet rs = stmt.executeQuery();
        List<Aihe> aiheet = new ArrayList<>();
        while (rs.next()) {
            Integer aiheid = rs.getInt("ID");
            String nimi = rs.getString("NIMI");
            Integer viestit = rs.getInt("VIESTEJA");
            String viimeisin = rs.getString("VIIMEISIN");
            String kirjoittaja = rs.getString("KIRJOITTAJA");
            String luotu = rs.getString("LUOTU");
            String kuvaus = rs.getString("KUVAUS");
            Integer alueid = rs.getInt("ALUE");

            aiheet.add(new Aihe(aiheid, alueid, kirjoittaja, nimi, kuvaus, luotu, viimeisin, viestit));
        }

        rs.close();
        stmt.close();
        connection.close();

        return aiheet;
    }
    // Tallentaa tietokantaan aiheen.

    public void save(Integer alueid, String nimi, String kuvaus, String kirjoittaja) throws Exception {
        if (kirjoittaja.isEmpty()) { //Asettaa tyhjän käyttäjänimen anonyymiksi.
            kirjoittaja = "Anonyymi";
        }
        Connection conn = database.getConnection();
        PreparedStatement aika = conn.prepareStatement("SELECT DATETIME('now', 'localtime')");
        ResultSet rs = aika.executeQuery();
        String luotu = rs.getString(1);
        aika.close();
        rs.close();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Aihe (alueid, nimi, kuvaus, kirjoittaja, luotu) VALUES(?, ?, ?, ?, ?)");
        stmt.setObject(1, alueid);
        stmt.setObject(2, nimi);
        stmt.setObject(3, kuvaus);
        stmt.setObject(4, kirjoittaja);
        stmt.setObject(5, luotu);

        stmt.execute();
        stmt.close();
        conn.close();
    }

    public Integer maxPage(Integer alueid) throws Exception {
        Connection conn = database.getConnection();
        PreparedStatement aiheet = conn.prepareStatement("SELECT COUNT(aihe.id) AS MAARA FROM aihe WHERE aihe.alueid = ?");
        aiheet.setObject(1, alueid);
        ResultSet rs = aiheet.executeQuery();
        boolean onko = rs.next();
        if (!onko) {
            return 0;
        }
        int maara = rs.getInt("MAARA") - 1;
        rs.close();
        aiheet.close();
        conn.close();
        return (maara / lkm) + 1;
    }

    //Palauttaa uusimman aiheen ID:n, jotta uuden aiheen aloitusviestin yhteydessä tiedetään mikä AIHEID viestille asetetaan.
    public Integer getMaxId() throws Exception {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT aihe.id FROM aihe ORDER BY id DESC LIMIT 1");
        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }
        int id = rs.getInt(1);

        rs.close();
        stmt.close();
        connection.close();
        return id;
    }
}
