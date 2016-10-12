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

        get("/", (req, res) -> { // Hakee indeksin
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/alueet", (req, res) -> {  //Hakee Forumissa olevat alueet.
            HashMap map = new HashMap<>();
            map.put("alueet", alueDao.getAlueet());

            return new ModelAndView(map, "alueet");
        }, new ThymeleafTemplateEngine());

        get("/alueet/:id", (req, res) -> { // Hakee alueeseen liittyvät aiheet.
            HashMap map = new HashMap<>();
            map.put("alue", alueDao.findOne(Integer.parseInt(req.params("id"))));
            map.put("aiheet", aiheDao.getAiheet(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine());
        
        get("/alue/luo", (req, res) -> { //Hakee alueen luomis sivun. Tässä ei tapahdu mitään.
            HashMap map = new HashMap<>();
            

            return new ModelAndView(map, "luoAlue");
        }, new ThymeleafTemplateEngine());
        
        post("/alue/luo", (req, res) -> { // Luo uuden alueen
            HashMap map = new HashMap<>();
            map.put("nimiPuuttuu", "Alueella täytyy olla nimi!");
            String nimi = req.queryParams("nimi");
            String kuvaus = req.queryParams("kuvaus");
            if(nimi.isEmpty()) {
                return new ModelAndView(map,  "luoAlue");
            }
            alueDao.save(nimi, kuvaus);
            res.redirect("/alueet");
            return new ModelAndView(map,  "luoAlue"); // Jos nimi on syötetty, niin tällä rivillä ei ole virkaa. Se vain estää punaisia alleviivauksia.
        }, new ThymeleafTemplateEngine());
        
        get("/alueet/:alueid/luo", (req, res) -> { // Hakee sivun jossa voi luoda uusia aiheita. TÄssä ei oikeastaan tapahdu mitään.
            HashMap map = new HashMap<>();
            

            return new ModelAndView(map, "luoAihe");
        }, new ThymeleafTemplateEngine());
        
         post("/alueet/:alueid/luo", (req, res) -> { // Luo uuden aiheen valittuun alueeseen.
            HashMap map = new HashMap<>();
            map.put("nimiPuuttuu", "Aiheella täytyy olla nimi!");
            String nimi = req.queryParams("nimi");
            String kuvaus = req.queryParams("kuvaus");
            String kirjoittaja = req.queryParams("kirjoittaja");
            if(nimi.isEmpty()) {
                return new ModelAndView(map,  "luoAihe");
            }
            aiheDao.save(Integer.parseInt(req.params("alueid")), nimi, kuvaus, kirjoittaja);
            res.redirect("/alueet/" +req.params("alueid"));
            return new ModelAndView(map,  "luoAihe");
        }, new ThymeleafTemplateEngine());
         
        get("/aiheet/:id/:page", (req, res) -> {  // Hakee aiheessa näytettävät viestit
            HashMap map = new HashMap<>();
            map.put("aihe", aiheDao.findOne(Integer.parseInt(req.params("id"))));
            // getViestit hakee nyt sivunumeron mukaisesti oikeat viestit.
            map.put("viestit", viestiDao.getViestit(Integer.parseInt(req.params("id")), Integer.parseInt(req.params("page"))));
            //Seuraava osa syöttää nettisivulle seuraavan sivun luvun page-muuttujana.
            map.put("next", Integer.parseInt(req.params("page")) +1);
            map.put("prev", Integer.parseInt(req.params("page")) -1);

            return new ModelAndView(map, "aihe");
        }, new ThymeleafTemplateEngine());
        
        post("/aiheet/:id/:page", (req, res) -> { // Ottaa vastaan aiheeseen lähetettyä tietoa. Uusia viestejä, näytettävien viestin lkm.
            if(!req.queryParams("viesti").isEmpty()){
                String nimi = req.queryParams("nimi");
                String viesti = req.queryParams("viesti");
                viestiDao.save(nimi, viesti, Integer.parseInt(req.params("id")));
            }
            if(req.queryParams("lkm") != null) {
                viestiDao.setLkm(Integer.parseInt(req.queryParams("lkm")));
            }
            res.redirect("/aiheet/" +Integer.parseInt(req.params("id")) +"/" +Integer.parseInt(req.params("page")));
            return "";
            
        });
        
        
    }
                
    }

   