package tikape.runko;


import java.util.HashMap;


import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.AiheDao;
import tikape.runko.database.AlueDao;
import tikape.runko.database.Database;
import tikape.runko.database.ViestiDao;


public class Main {

    // Maksimipituudet foorumiin syÃ¶tettÃ¤ville teksteille.
    // Final ja private, jottei sitÃ¤ voida muuttaa kesken kaiken.
    private static final int maxNimiPituus = 20;
    private static final int maxOtsikkoPituus = 50;
    private static final int maxKuvausPituus = 200;
    private static final int maxViestiPituus = 9001; // Hehe :D

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:forum.db");

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

        get("/alueet/:id", (req, res) -> { // Hakee alueeseen liittyvÃ¤t aiheet.
            HashMap map = new HashMap<>();
            map.put("alue", alueDao.findOne(Integer.parseInt(req.params("id"))));
            if (aiheDao.getAiheet(Integer.parseInt(req.params("id"))).get(0).getNimi() == null) { // Jos ensimmäisen aiheen nimi on tyhjä, aiheita ei ole.
                map.put("eiAiheita", "EI AIHEITA");
                System.out.println("Ei ollut aiheita");
            } else {
                map.put("aiheet", aiheDao.getAiheet(Integer.parseInt(req.params("id"))));
            }

            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine());

        get("/alue/luo", (req, res) -> { //Hakee alueen luomis sivun. TÃ¤ssÃ¤ ei tapahdu mitÃ¤Ã¤n.
            HashMap map = new HashMap<>();

            return new ModelAndView(map, "luoAlue");
        }, new ThymeleafTemplateEngine());

        post("/alue/luo", (req, res) -> { // Luo uuden alueen
            HashMap map = new HashMap<>();
            String nimi = req.queryParams("nimi");
            String kuvaus = req.queryParams("kuvaus");
            nimi = nimi.trim();
            kuvaus = kuvaus.trim();
            // Jos nimikenttÃ¤ on tyhjÃ¤, ladataan sivu uudestaan, tÃ¤llÃ¤kertaa HashMapilla, jossa on "nimipuuttuu" olio.
            if (nimi.isEmpty()) {
                map.put("virheilmoitus", "Alueella täytyy olla nimi!");

                return new ModelAndView(map, "luoAlue");
            } // Sama, jos nimi on liian pitkÃ¤.
            if (nimi.length() > maxOtsikkoPituus) {
                map.put("virheilmoitus", "Alueen nimi voi olla korkeintaan " + maxOtsikkoPituus
                        + " merkkiä!");
                return new ModelAndView(map, "luoAlue");
            }
            if (kuvaus.length() > maxKuvausPituus) {
                map.put("virheilmoitus", "Kuvauksen nimi voi olla korkeintaan " + maxKuvausPituus
                        + " merkkiä!");
                return new ModelAndView(map, "luoAlue");
            }
            alueDao.save(nimi, kuvaus);
            res.redirect("/alueet");
            return new ModelAndView(map, "luoAlue"); // Jos nimi on syötetty, niin tällä rivillä ei ole virkaa. Se vain estää punaisia alleviivauksia.
        }, new ThymeleafTemplateEngine());

        get("/alueet/:alueid/luo", (req, res) -> { // Hakee sivun jossa voi luoda uusia aiheita. TÃ„ssÃ¤ ei oikeastaan tapahdu mitÃ¤Ã¤n.
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
            viesti = viesti.trim();
            nimi = nimi.trim();
            kuvaus = kuvaus.trim();
            kirjoittaja = kirjoittaja.trim();
            // Tarkistetaan ensimmÃ¤iseksi ovatko jotkut vaaditut kentät tyhjiä.
            if (nimi.isEmpty() && viesti.isEmpty()) {
                map.put("nimiviestiPuuttuu", "Aiheella täytyy olla nimi ja aloitusviesti!");
                return new ModelAndView(map, "luoAihe");
            } else if (nimi.isEmpty()) {
                map.put("nimiPuuttuu", "Aiheella täytyy olla nimi!");
                return new ModelAndView(map, "luoAihe");
            } else if (viesti.isEmpty()) {
                map.put("viestiPuuttuu", "Kirjoita aiheelle aloitusviesti!");
                return new ModelAndView(map, "luoAihe");
            }
            // Seuraavaksi tarkistetaan ovatko jotkin kentät liian täysiä.
            if(nimi.length() > maxOtsikkoPituus){
                map.put("liianPitka","Aiheen otsikko voi olla korkeintaan " + maxOtsikkoPituus
                + " merkkiä!");
                return new ModelAndView(map,"luoAihe");
            }
            if(kirjoittaja.length() > maxNimiPituus){
                map.put("liianPitka","Kirjoittajan nimi voi olla korkeintaan " + maxNimiPituus
                + " merkkiä!");
                return new ModelAndView(map,"luoAihe");
            }
            if(kuvaus.length() > maxKuvausPituus){
                map.put("liianPitka","Kuvaus voi olla korkeintaan " + maxKuvausPituus
                + " merkkiä!");
                return new ModelAndView(map,"luoAihe");
            }
            if(viesti.length() > maxViestiPituus){
                map.put("liianPitka","Viesti voi olla korkeintaan " + maxViestiPituus
                + " merkkiä!");
                return new ModelAndView(map,"luoAihe");
            }

            aiheDao.save(Integer.parseInt(req.params("alueid")), nimi, kuvaus, kirjoittaja);
            viestiDao.save(kirjoittaja, viesti, aiheDao.getMaxId());
            res.redirect("/alueet/" + req.params("alueid"));
            return new ModelAndView(map, "luoAihe");
        }, new ThymeleafTemplateEngine());

        get("/alueet/:alueid/aiheet/:id/:page", (req, res) -> {  // Hakee aiheessa nÃ¤ytettÃ¤vÃ¤t viestit
            HashMap map = new HashMap<>();
            map.put("alue", alueDao.findOne(Integer.parseInt(req.params("alueid"))));
            map.put("aihe", aiheDao.findOne(Integer.parseInt(req.params("id"))));
            // getViestit hakee nyt sivunumeron mukaisesti oikeat viestit.
            map.put("viestit", viestiDao.getViestit(Integer.parseInt(req.params("id")), Integer.parseInt(req.params("page"))));
            //Seuraava osa syÃ¶ttÃ¤Ã¤ nettisivulle seuraavan sivun luvun page-muuttujana.
            // Jos sivunumero = maxPage, eli pienin sivunumero, jolla aiheen viimeiset viestit nÃ¤kyvÃ¤t
            // asetetaan "next"in arvolle sama arvo kuin nykyisellÃ¤ sivulla.
            // Jos sivunumero = 1, niin "prev"in arvoksi asetetaan 1, jotta sivunumero ei voi laskea nollaan ja sitÃ¤ alemmas.
            int maxPage = viestiDao.maxPage(Integer.parseInt(req.params("id")));
            if (Integer.parseInt(req.params("page")) < maxPage) {
                map.put("next", Integer.parseInt(req.params("page")) + 1);
            } else {
                map.put("next", maxPage);
            }
            if (Integer.parseInt(req.params("page")) > 1) {
                map.put("prev", Integer.parseInt(req.params("page")) - 1);
            } else {
                map.put("prev", 1);
            }
            map.put("last", maxPage);
            // Olisiko syytÃ¤ laittaa Edellinen aihe - Seuraava aihe selaus? Se tuntuisi tosin olevan hieman tavallista hankalempaa,
            // eikÃ¤ ole niin sanotusti vaadittu tehtÃ¤vÃ¤nannossa.

            return new ModelAndView(map, "aihe");
        }, new ThymeleafTemplateEngine());

        post("/alueet/:alueid/aiheet/:id/:page", (req, res) -> { // Ottaa vastaan aiheeseen lÃ¤hetettyÃ¤ tietoa. Uusia viestejÃ¤, nÃ¤ytettÃ¤vien viestin lkm.
            HashMap map = new HashMap<>();
            map.put("aihe", aiheDao.findOne(Integer.parseInt(req.params("id"))));
            map.put("viestit", viestiDao.getViestit(Integer.parseInt(req.params("id")), Integer.parseInt(req.params("page"))));
            int maxPage = viestiDao.maxPage(Integer.parseInt(req.params("id")));
            if (Integer.parseInt(req.params("page")) < maxPage) {
                map.put("next", Integer.parseInt(req.params("page")) + 1);
            } else {
                map.put("next", maxPage);
            }
            if (Integer.parseInt(req.params("page")) > 1) {
                map.put("prev", Integer.parseInt(req.params("page")) - 1);
            } else {
                map.put("prev", 1);
            }
            map.put("last", maxPage);
            if (req.queryParams("viesti") != null) {
                if (req.queryParams("viesti").length() > maxViestiPituus) {
                    map.put("liianPitka","Viesti voi olla korkeintaan " + maxViestiPituus
                    + " merkkiä!");
                    return new ModelAndView(map, "aihe");
                } else if (!req.queryParams("viesti").trim().isEmpty()) { // if-lauseet pitÃ¤vÃ¤t huolen ettÃ¤ data syÃ¶tetÃ¤Ã¤n oikeassa muodossa.
                    String nimi = req.queryParams("nimi"); // tÃ¤Ã¤llÃ¤kin voisi muokata if-lauseita ottamaan huomioon tietokanna varChar vaatimukset.
                    String viesti = req.queryParams("viesti");
                    viestiDao.save(nimi, viesti, Integer.parseInt(req.params("id")));
                }
            }
            if (req.queryParams("lkm") != null) {
                viestiDao.setLkm(Integer.parseInt(req.queryParams("lkm")));
                res.redirect("/alueet/" +Integer.parseInt(req.params("alueid")) +"/aiheet/" + Integer.parseInt(req.params("id")) + "/" + Integer.parseInt(req.params("page")));
            }
            res.redirect("/alueet/"+Integer.parseInt(req.params("alueid")) +"/aiheet/" + Integer.parseInt(req.params("id")) + "/" + viestiDao.maxPage(Integer.parseInt(req.params("id"))));
            return new ModelAndView(map, "aihe");

        }, new ThymeleafTemplateEngine());

    }

}
