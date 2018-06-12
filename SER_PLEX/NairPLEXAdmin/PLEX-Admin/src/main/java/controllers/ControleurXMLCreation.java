package controllers;

import ch.heigvd.iict.ser.imdb.models.Role;
import models.*;
import org.dom4j.*;
import org.jdom2.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import views.*;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.thoughtworks.xstream.XStream;

public class ControleurXMLCreation {

    private ControleurXMLCreation2 controleurXMLCreation2 = null;

    //private ControleurGeneral ctrGeneral;
    private static MainGUI mainGUI;
    private ORMAccess ormAccess;

    private GlobalData globalData;

    public ControleurXMLCreation(ControleurGeneral ctrGeneral, MainGUI mainGUI, ORMAccess ormAccess){
        //this.ctrGeneral=ctrGeneral;
        ControleurXMLCreation.mainGUI=mainGUI;
        this.ormAccess=ormAccess;
    }

    public void createXML(){
        new Thread(){
            public void run(){
                mainGUI.setAcknoledgeMessage("Creation XML... WAIT");
                long currentTime = System.currentTimeMillis();
                try {

                    globalData = ormAccess.GET_GLOBAL_DATA();
                    XMLOutputter outp = new XMLOutputter(Format.getPrettyFormat());
                    Element projections = new Element("projections");

                    List<Projection> listeProjection = globalData.getProjections();
                    for(Projection projection : listeProjection){
                        //Création d'un élément représentant la projection courante de la boucle
                        Element projection1 = new Element("projection");
                        //Création d'un élément représentant la salle de projéction courante de la bouecle
                        Element salle = new Element("salle");
                        //Idem au deux commentaire précédent
                        Element film = new Element("film");
                        //Ajout du texte contenu entre les balise de l'élément salle
                        salle.setText(projection.getSalle().toString());
                        //Ajout des deux attribut de la balise Salle
                        salle.setAttribute("no",projection.getSalle().getNo());
                        salle.setAttribute("taille", String.valueOf(projection.getSalle().getTaille()));


                        /************* nouvel ajout ***************************/
                        final String sFormatDate = "mm:dd:yyyy:hh:mm";

                        DateFormat df = new SimpleDateFormat(sFormatDate);
                        Date dProjection = projection.getDateHeure().getTime();
                        String sDateProjection = df.format(dProjection);

                        Element eDateHeure = new Element("dateHeure");
                        eDateHeure.setText(sDateProjection);
                        eDateHeure.setAttribute("format", "mm:dd:yyyy:hh:mm");
                        /******************************************************/


                        //Idem à la salle
                        /*****************************************************************/
                        film.setAttribute("idFilm", String.valueOf(projection.getFilm().getId()));
                        /****************************************************************/


                        film.setAttribute("titre", projection.getFilm().getTitre());
                        film.setAttribute("duree", String.valueOf(projection.getFilm().getDuree()));
                        film.setText(projection.getFilm().getSynopsis());


                        /*****************************************************************/
                        final String sPathPhoto = "http://docr.iict.ch/imdb/";
                        final String sFormatPhoto = ".jpg";
                        Element ePhoto = new Element("photo");
                        ePhoto.setAttribute("url", sPathPhoto + projection.getFilm().getId() + sFormatPhoto);
                        /*****************************************************************/


                        film.addContent(ePhoto);

                        //Création d'un élément critiques contenant tous les élements critique
                        Element critiques = new Element("critiques");

                        //Boucle tant que la projection courante possède des critiques
                        for(Critique critique : projection.getFilm().getCritiques()){
                            //Création d'un élément critique
                            Element critique1 = new Element("critique");
                            //Crtéation d'un élément texte
                            Element text = new Element("text");
                            //Ajout de l'attribut note à la critique
                            critique1.setAttribute("note", String.valueOf(critique.getNote()));
                            text.addContent(critique.getTexte());
                            critique1.addContent(text);
                            critiques.addContent(critique1);
                        }
                        //Ajout de toutes les critiques au film courant
                        film.addContent(critiques);

                        //Boucle permettant d'ajouter tous les genres
                        for(Genre genre : projection.getFilm().getGenres()){
                            Element genre1 = new Element("genre");


                            /****************************************************/
                            genre1.setAttribute("idGenre", String.valueOf(genre.getId()));

                            genre1.setAttribute("genreFilm", genre.getLabel());
                            film.addContent(genre1);
                        }

                        //Boucle permettant d'ajouter toutes les langues
                        for (Langage langue : projection.getFilm().getLangages()){
                            Element language = new Element("language");


                            /****************************************************/
                            language.setAttribute("idLanguage", String.valueOf(langue.getId()));
                            /****************************************************/


                            language.setAttribute("langue", langue.getLabel());
                            film.addContent(language);
                        }

                        Element roles = new Element("roles");

                        for (RoleActeur role : projection.getFilm().getRoles()){
                            Element role1 = new Element("role");
                            Element acteur = new Element("acteur");
                            Element biographie = new Element("biographie");
                            role1.setAttribute("place", String.valueOf(role.getPlace()));


                            /**********************************************************/
                            acteur.setAttribute("idActeur", String.valueOf(role.getActeur().getId()));
                            if(role.getPersonnage() != null){
                                acteur.setAttribute("personnage", role.getPersonnage());
                            }else{
                                acteur.setAttribute("personnage", "");
                            }

                            /**********************************************************/


                            acteur.setAttribute("nom", role.getActeur().getNom());
                            //Affichage de l'attribut nomNaissance uniquement si il y en a un
                            if(role.getActeur().getNomNaissance() != null){
                                acteur.setAttribute("nomNaissance", role.getActeur().getNomNaissance());
                            }
                            else {
                                acteur.setAttribute("nomNaissance", "");
                            }
                            acteur.setAttribute("sexe", String.valueOf(role.getActeur().getSexe()));

                            acteur.setAttribute("format","yyyy:mm:dd");

                            //Affichage de la date de naissance uniquement si il y en a une
                            if(role.getActeur().getDateNaissance() != null){
                                //Intérpétation de la date de naissance sous la forme Année-Mois-Jour
                                Calendar dateNaissance = role.getActeur().getDateNaissance();
                                int annee = dateNaissance.get(Calendar.YEAR);
                                int mois = dateNaissance.get(Calendar.MONTH);
                                int jour = dateNaissance.get(Calendar.DAY_OF_MONTH);
                                acteur.setAttribute("dateNaissance", annee + ":" + mois + ":" + jour );
                            }
                            else{
                                acteur.setAttribute("dateNaissance", "");
                            }

                            if(role.getActeur().getDateDeces() != null){
                                Calendar dateDeces = role.getActeur().getDateDeces();
                                int annee = dateDeces.get(Calendar.YEAR);
                                int mois = dateDeces.get(Calendar.MONTH);
                                int jour = dateDeces.get(Calendar.DAY_OF_MONTH);
                                acteur.setAttribute("dateDeces", annee + ":" + mois + ":" + jour );
                            }else{
                                acteur.setAttribute("dateDeces", "");
                            }


                            //Ajout du texte de la biographie
                            biographie.setText(role.getActeur().getBiographie());
                            //Ajout de la biographie dans la balise acteur
                            acteur.addContent(biographie);
                            //Ajout de l'acteur dans la balise role
                            role1.addContent(acteur);
                            //Ajout de role dans la balise roles
                            roles.addContent(role1);

                            //Boucle permettant d'ajouter tous les mots clées
                            for(Motcle motcle : projection.getFilm().getMotcles()){
                                Element motCle = new Element("motCle");


                                /******************************************************/
                                motCle.setAttribute("idMotCle", String.valueOf(motcle.getId()));
                                /******************************************************/


                                motCle.setAttribute("label", motcle.getLabel());
                                film.addContent(motCle);
                            }
                        }

                        film.addContent(roles);

                        //Ajout des éléments salle et filme à la projection courante
                        projection1.addContent(salle);
                        projection1.addContent(eDateHeure);
                        projection1.addContent(film);

                        //Ajout de la projection courante à la liste de toute les projections
                        projections.addContent(projection1);
                    }

                    //Création d'un document contenant toutes les projections
                    Document doc = new Document(projections);
                    outp.output(doc, new FileOutputStream("SER_XML_Labo2.xml"));

                }catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    mainGUI.setErrorMessage("Construction XML impossible", e.toString());
                }
                System.out.println("Done [" + displaySeconds(currentTime, System.currentTimeMillis()) + "]");
                mainGUI.setAcknoledgeMessage("XML cree en "+ displaySeconds(currentTime, System.currentTimeMillis()) );

            }
        }.start();
    }

    public void createXStreamXML(){
        new Thread(){
            public void run(){
                mainGUI.setAcknoledgeMessage("Creation XML... WAIT");
                long currentTime = System.currentTimeMillis();
                try {
                    globalData = ormAccess.GET_GLOBAL_DATA();
                    globalDataControle();
                }
                catch (Exception e){
                    mainGUI.setErrorMessage("Construction XML impossible", e.toString());
                }

                XStream xstream = new XStream();
                writeToFile("global_data.xml", xstream, globalData);
                System.out.println("Done [" + displaySeconds(currentTime, System.currentTimeMillis()) + "]");
                mainGUI.setAcknoledgeMessage("XML cree en "+ displaySeconds(currentTime, System.currentTimeMillis()) );



            }
        }.start();
    }

    private static void writeToFile(String filename, XStream serializer, Object data) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
            serializer.toXML(data, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final DecimalFormat doubleFormat = new DecimalFormat("#.#");
    private static final String displaySeconds(long start, long end) {
        long diff = Math.abs(end - start);
        double seconds = ((double) diff) / 1000.0;
        return doubleFormat.format(seconds) + " s";
    }

    private void globalDataControle(){
        for (Projection p:globalData.getProjections()){
            System.out.println("******************************************");
            System.out.println(p.getFilm().getTitre());
            System.out.println(p.getSalle().getNo());
            System.out.println("Acteurs *********");
            for(RoleActeur role : p.getFilm().getRoles()) {
                System.out.println(role.getActeur().getNom());
            }
            System.out.println("Genres *********");
            for(Genre genre : p.getFilm().getGenres()) {
                System.out.println(genre.getLabel());
            }
            System.out.println("Mot-cles *********");
            for(Motcle motcle : p.getFilm().getMotcles()) {
                System.out.println(motcle.getLabel());
            }
            System.out.println("Langages *********");
            for(Langage langage : p.getFilm().getLangages()) {
                System.out.println(langage.getLabel());
            }
            System.out.println("Critiques *********");
            for(Critique critique : p.getFilm().getCritiques()) {
                System.out.println(critique.getNote());
                System.out.println(critique.getTexte());
            }
        }
    }
}