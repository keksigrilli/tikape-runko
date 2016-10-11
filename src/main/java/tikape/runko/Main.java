package tikape.runko;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.AiheDao;
import tikape.runko.database.AlueDao;
import tikape.runko.database.Database;
import tikape.runko.database.OpiskelijaDao;
import tikape.runko.database.ViestiDao;
import tikape.runko.domain.Aihe;
import tikape.runko.domain.Alue;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:forum.db");
        
        //Tämä metodi luo automaattisesti Opiskelija -tietokannan.
        // Katsokaa Database -luokasta sen toiminnallisuuta,
        // olisi kiva jos joku automatisoisi Forum -tietokannan luomisen sinne.
        // Mutta katsokaa siinä tapauksessa tarkkaan Forumin sarakkeiden nimet ja määrät,
        // Ne eivät täsmää 100% raportissa määritellyihin CREATE TABLE -lauseihin.
        database.init();
        OpiskelijaDao opiskelijaDao = new OpiskelijaDao(database);

        AlueDao alueDao = new AlueDao(database);
        AiheDao aiheDao = new AiheDao(database);
        ViestiDao viestiDao = new ViestiDao(database);
        
        // run:ssa koodattuna yksinkertainen tekstikäyttöliittymä foorumille.
        // SITÄ SAA KEHITTÄÄ JA PARANNELLA :P
        //run(alueDao, aiheDao, viestiDao);
        // Kommentoituna localhostin toimintaa. En vielä jaksanut perehtyä siihen ollenkaan.
        // Tärkeintä on mielestäni saada tekstikäyttöliittymä ensin toimimaan tärkeimmillä toiminnoilla.
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/alueet", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("alueet", alueDao.getAlueet());

            return new ModelAndView(map, "alueet");
        }, new ThymeleafTemplateEngine());

        get("/alueet/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("alue", alueDao.findOne(Integer.parseInt(req.params("id"))));
            map.put("aiheet", aiheDao.getAiheet(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine());
        
        get("/aihe/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("aihe", aiheDao.findOne(Integer.parseInt(req.params("id"))));
            map.put("viestit", viestiDao.getViestit(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "aihe");
        }, new ThymeleafTemplateEngine());
        
        post("/aihe/:id", (req, res) -> {
            String nimi = req.queryParams("nimi");
            String viesti = req.queryParams("viesti");
            viestiDao.save(nimi, viesti, Integer.parseInt(req.params("id")));
            
            res.redirect("/aihe/" +Integer.parseInt(req.params("id")));
            
            return "";
            
        });
        
    }
                
    }

    /*private static void run(AlueDao alueDao, AiheDao aiheDao, ViestiDao viestiDao) throws SQLException {
        //Tässä on nyt nopea käyttötapausten toteutukset tekstikäyttöliittymällä
        //huomasin tehdessäni tätä, että sarakkeiden nimeäminen on turhaa,
        //sillä SQL kysely ei palauta sarakkeiden nimiä, vain itse sarakkeet..
        //Sarakkeiden nimet pitäisi siis saada näkymään joko ohjelmallisesti,
        //tai sitten pitäisi lukea materiaalista tarkemmat ohjeet.
        //Jokatapauksessa kun koodi siirretään nettiin, luulisi sarakkeiden nimet hoituvan helposti HTML koodilla.
        //Käyttötapaus 1/3 hakeminen
        List<Alue> alueet = alueDao.getAlueet();
        Scanner lukija = new Scanner(System.in);
        for (Alue alue : alueet) {
            System.out.println(alue.getNimi() +"\t" +alue.getViestit() +"\t" +alue.getViimeisin());
        }
        //Käyttötapaus 2/3
        System.out.println("Valitse alue:");
        String alue = lukija.nextLine();
        List<Aihe> aiheet = aiheDao.getAiheet(alue);
        for (Aihe aihe : aiheet) {
            System.out.println(aihe.getNimi() +"\t" +aihe.getViestit() +"\t" +aihe.getViimeisin());   
        }
        //Käyttötapaus 3/3
        System.out.println("Valitse aihe: ");
        String aihe = lukija.nextLine();
        List<String> viestit = viestiDao.getViestit(aihe);
        for (String viesti : viestit) {
            System.out.println(viesti);   
        }
    }*/

