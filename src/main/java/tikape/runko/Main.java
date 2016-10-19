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

        AlueDao alueDao = new AlueDao(database);
        AiheDao aiheDao = new AiheDao(database);
        ViestiDao viestiDao = new ViestiDao(database);

        get("/", (req, res) -> { // Hakee indeksin
            HashMap map = new HashMap<>();
            map.put("viesti", "Tervetuloa Derail-keskustelufoorumille!");

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
            if(nimi.isEmpty()) { // Jos nimikenttä on tyhjä, ladataan sivu uudestaan, tälläkertaa HashMapilla, jossa on "nimipuuttuu" olio.
                return new ModelAndView(map,  "luoAlue");
            }
            alueDao.save(nimi, kuvaus);
            res.redirect("/alueet");
            return new ModelAndView(map,  "luoAlue"); // Jos nimi on syötetty, niin tällä rivillä ei ole virkaa. Se vain estää punaisia alleviivauksia.
        }, new ThymeleafTemplateEngine());
        
        get("/alueet/:alueid/luo", (req, res) -> { // Hakee sivun jossa voi luoda uusia aiheita. TÄssä ei oikeastaan tapahdu mitään.
            HashMap map = new HashMap<>();
            map.put("takas", req.params("alueid"));
            

            return new ModelAndView(map, "luoAihe");
        }, new ThymeleafTemplateEngine());
        
         post("/alueet/:alueid/luo", (req, res) -> { // Luo uuden aiheen valittuun alueeseen.
            HashMap map = new HashMap<>();
            String viesti = req.queryParams("viesti");
            String nimi = req.queryParams("nimi");
            String kuvaus = req.queryParams("kuvaus");
            String kirjoittaja = req.queryParams("kirjoittaja");
            if(nimi.isEmpty() && viesti.isEmpty()) { // Tarkistetaan täyttyvätkö ehdot. TÄTÄ VOISI MUOKATA TIETOKANNAN VARCHAR-MUUTTUJIEN MUKAISEKSI.
                map.put("nimiviestiPuuttuu", "Aiheella täytyy olla nimi ja aloitusviesti!");
                return new ModelAndView(map, "luoAihe");
            } else if(nimi.isEmpty()) {
                map.put("nimiPuuttuu", "Aiheella täytyy olla nimi!");
                return new ModelAndView(map,  "luoAihe");
            } else if(viesti.isEmpty()) {
                map.put("viestiPuuttuu", "Kirjoita aiheelle aloitusviesti!");
                return new ModelAndView(map, "luoAihe");
            }
            
            aiheDao.save(Integer.parseInt(req.params("alueid")), nimi, kuvaus, kirjoittaja);
            viestiDao.save(kirjoittaja, viesti, aiheDao.getMaxId());
            res.redirect("/alueet/" +req.params("alueid"));
            return new ModelAndView(map,  "luoAihe");
        }, new ThymeleafTemplateEngine());
         
        get("/aiheet/:id/:page", (req, res) -> {  // Hakee aiheessa näytettävät viestit
            HashMap map = new HashMap<>();
            map.put("aihe", aiheDao.findOne(Integer.parseInt(req.params("id"))));
            // getViestit hakee nyt sivunumeron mukaisesti oikeat viestit.
            map.put("viestit", viestiDao.getViestit(Integer.parseInt(req.params("id")), Integer.parseInt(req.params("page"))));
            //Seuraava osa syöttää nettisivulle seuraavan sivun luvun page-muuttujana.
            // Jos sivunumero = maxPage, eli pienin sivunumero, jolla aiheen viimeiset viestit näkyvät
            // asetetaan "next"in arvolle sama arvo kuin nykyisellä sivulla.
            // Jos sivunumero = 1, niin "prev"in arvoksi asetetaan 1, jotta sivunumero ei voi laskea nollaan ja sitä alemmas.
            int maxPage = viestiDao.maxPage(Integer.parseInt(req.params("id")));
            if(Integer.parseInt(req.params("page"))< maxPage  ){
                map.put("next", Integer.parseInt(req.params("page")) +1);
            } else {
                map.put("next", maxPage);
            } if(Integer.parseInt(req.params("page")) > 1) {
                map.put("prev", Integer.parseInt(req.params("page")) -1);
            } else {
                map.put("prev", 1);
            }
            map.put("last", maxPage);
            // Olisiko syytä laittaa Edellinen aihe - Seuraava aihe selaus? Se tuntuisi tosin olevan hieman tavallista hankalempaa,
            // eikä ole niin sanotusti vaadittu tehtävänannossa.

            return new ModelAndView(map, "aihe");
        }, new ThymeleafTemplateEngine());
        
        post("/aiheet/:id/:page", (req, res) -> { // Ottaa vastaan aiheeseen lähetettyä tietoa. Uusia viestejä, näytettävien viestin lkm.
            if(req.queryParams("viesti") != null){ // if-lauseet pitävät huolen että data syötetään oikeassa muodossa.
                String nimi = req.queryParams("nimi"); // täälläkin voisi muokata if-lauseita ottamaan huomioon tietokanna varChar vaatimukset.
                String viesti = req.queryParams("viesti");
                viestiDao.save(nimi, viesti, Integer.parseInt(req.params("id")));
            }
            if(req.queryParams("lkm") != null) {
                viestiDao.setLkm(Integer.parseInt(req.queryParams("lkm")));
                res.redirect("/aiheet/" +Integer.parseInt(req.params("id")) +"/" +Integer.parseInt(req.params("page")));
            }
            res.redirect("/aiheet/" +Integer.parseInt(req.params("id")) +"/" +viestiDao.maxPage(Integer.parseInt(req.params("id"))));
            return "";
            
        });
        
        
    }
                
    }

   