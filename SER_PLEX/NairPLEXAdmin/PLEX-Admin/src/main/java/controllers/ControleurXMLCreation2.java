package controllers;

import models.*;
import org.jdom2.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.ProcessingInstruction;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.xml.sax.helpers.DefaultHandler;
import views.*;

import java.io.*;
import java.util.*;

public class ControleurXMLCreation2 extends DefaultHandler{

    //private ControleurGeneral ctrGeneral;
    private static MainGUI mainGUI;
    private ORMAccess ormAccess;

    private GlobalData globalData;

    public ControleurXMLCreation2() throws JDOMException, IOException {


        /* On lit le fichier source */
        SAXBuilder builder = new SAXBuilder();
        Document docSource = builder.build(new File("SER_XML_Labo2.xml"));

        // Construction du document résultat
        Document document = new Document();

        /* Ajout de la référence à la DTD selon annexe */
        DocType docType = new DocType("plex", "projections.dtd");
        document.addContent(docType);

        /* Ajout de la référence à la feuille XSL selon annexe */
        ProcessingInstruction piXSL = new ProcessingInstruction("xml-stylesheet");
        HashMap<String, String> piAttributes = new HashMap<String, String>();
        piAttributes.put("type", "text/xsl");
        piAttributes.put("href", "projections.xsl");
        piXSL.setData(piAttributes);
        document.addContent(piXSL);

        /* On ajoute l'élément "plex" en tant que racine */
        Element ePlex = new Element("plex");
        document.addContent(ePlex);

        Element eProjections = new Element("projections");
        ePlex.addContent(eProjections);

        /* On va chercher toutes les projections  */
        XPathFactory XPathFac = XPathFactory.instance();
        XPathExpression XPathProjection = XPathFac.compile("//projection", Filters.element());

        /* On va chercher toutes les projections  */
        List<Element> lProjections = XPathProjection.evaluate(docSource);

        /* On recherhce tous les films */
        XPathExpression XPathFilm = XPathFac.compile("//film", Filters.element());

        /* On stocke le résultat sous forme de liste */
        List<Element> lFilms = XPathFilm.evaluate(docSource);

        HashMap<String, String> hmFilms = new HashMap<String, String>();
        ArrayList alFilm = new ArrayList();
        HashMap<Integer, String> hmActeurs = new HashMap<Integer, String>();
        HashMap<Integer, String> hmCheckActeurs = new HashMap<Integer, String>();

        /* Recherche de tous les acteurs */
        XPathExpression XPathCheckActeurs = XPathFac.compile("//acteur", Filters.element());

        /* On stocke le résultat sous forme de liste */
        List<Element> lCheckActeurs = XPathCheckActeurs.evaluate(docSource);

        HashMap<Integer, String> hmMapLangages = new HashMap<Integer, String>();
        ArrayList alLangagesI = new ArrayList();
        ArrayList alLangagesS = new ArrayList();
        HashMap<Integer, String> hmMapGenres = new HashMap<Integer, String>();
        ArrayList alGenresI = new ArrayList();
        ArrayList alGenresS = new ArrayList();
        HashMap<Integer, String> hmMapMotsCles = new HashMap<Integer, String>();
        ArrayList alMotsClesI = new ArrayList();
        ArrayList alMotsClesS = new ArrayList();

        Element eProjection;
        Element eSalle;
        Element eDateHeure;
        Element eFilms;
        Element eFilm;
        Element eTitre;
        Element eDuree;
        Element eSynopsis;
        Element ePhoto;
        Element eCritiques;
        Element eCritique;
        Element eLangages;
        Element eGenres;
        Element eMotCles;
        Element eRoles;
        Element eRole;
        Element eActeurs;
        Element eActeur;
        Element eNom;
        Element eNomNaissance;
        Element eSexe;
        Element eDateNaissance;
        Element eDateDeces;
        Element eBiographie;
        Element eListeLangages;
        Element eLangage;
        Element eListeGenres;
        Element eGenre;
        Element eListeMotsCles;
        Element eMotCle;

        //Parcours des projections
        for(Element projection : lProjections){
            //Création des projections
            eProjection = new Element("projection");
            eProjection.setAttribute("idFilm", "F" + projection.getChild("film").getAttributeValue("idFilm"));
            eProjection.setAttribute("titre", projection.getChild("film").getAttributeValue("titre"));

            hmFilms.put(eProjection.getAttributeValue("titre"), eProjection.getAttributeValue("idFilm"));

            //Création des salles
            eSalle = new Element("salle");
            if(projection.getChild("salle").getText() != null){
                eSalle.setText(projection.getChild("salle").getText());
            }
            eSalle.setAttribute("taille", projection.getChild("salle").getAttributeValue("taille"));
            eProjection.addContent(eSalle);

            //Création des dateHeures
            eDateHeure = new Element("dateHeure");
            eDateHeure.setText(projection.getChild("dateHeure").getText());
            eDateHeure.setAttribute("format", projection.getChild("dateHeure").getAttributeValue("format"));
            eProjection.addContent(eDateHeure);

            eProjections.addContent(eProjection);
        }

        eFilms = new Element("films");

        for(int i = 0; i < lCheckActeurs.size(); ++i){
            hmCheckActeurs.put(Integer.valueOf(lCheckActeurs.get(i).getAttributeValue("idActeur")), "temp");
        }
        //Ajout de la balise films
        ePlex.addContent(eFilms);
        Vector<String>vTitreFilms = new Vector<String>();
        //Parcours des films
        for(Element film : lFilms){
            String sTitre = String.valueOf(hmFilms.get(film.getAttributeValue("titre")));
            String sTitreSansF = String.valueOf(hmFilms.get(film.getAttributeValue("titre"))).substring(1);
            if(!sTitre.equals("null")){
                //Création des balises et éléments film avec les attributs et le texte
                eFilm = new Element("film");
                eFilm.setAttribute("no", sTitre);
                hmFilms.put(film.getAttributeValue("titre"), null);
                vTitreFilms.add(film.getAttributeValue("idFilm"));

                //Ajout du texte dans la balise film
                eTitre = new Element("titre");
                if(film.getAttributeValue("titre") != null){
                    eTitre.setText(film.getAttributeValue("titre"));
                }
                alFilm.add(film.getAttributeValue("titre"));
                eFilm.addContent(eTitre);

                //Création de la balise durée avec les attributs et le texte s'il y en a
                eDuree = new Element("duree");
                if(film.getAttributeValue("duree") != null){
                    eDuree.setText(film.getAttributeValue("duree"));
                }
                eDuree.setAttribute("format", "minutes");
                eFilm.addContent(eDuree);

                //Création de la balise synopsys avec le texte s'il y en a
                eSynopsis = new Element("synopsys");
                if(film.getText() != null){
                    eSynopsis.setText(film.getText());
                }
                eFilm.addContent(eSynopsis);

                //Création de la balise photo avec son attribut url s'il y en a une
                if(film.getChild("photo").getAttributeValue("url") != null){
                    ePhoto = new Element("photo");
                    ePhoto.setAttribute("url", film.getChild("photo").getAttributeValue("url"));
                    eFilm.addContent(ePhoto);
                }

                //Recherche de toutes les critiques par rapport au film de la variable sTitreSansF, puis résultat sous forme de liste
                String sTemp = "/projections/projection/film[attribute::idFilm='" + sTitreSansF + "']/critiques/critique";
                XPathExpression xpCritique = XPathFac.compile(sTemp);
                List<Element> lCritiques = xpCritique.evaluate(docSource);
                HashMap<String, Boolean> hmCritiques = new HashMap<String, Boolean>();

                //Création de la balise critiques
                eCritiques = new Element("critiques");
                eFilm.addContent(eCritiques);

                StringBuffer sbCritiques = new StringBuffer();
                //Parcours des différentes critiques
                for(Element critique : lCritiques){

                    //Création de la balise critique avec ses attributs et son texte s'il y en a un
                    eCritique = new Element("critique");
                    String sNote = critique.getAttributeValue("note");
                    String sComment = critique.getText();
                    if(hmCritiques.get(sNote+sComment) == null){
                        if(critique.getText() != null){
                            eCritique.setText(critique.getText());
                        }
                        eCritique.setAttribute("note", critique.getAttributeValue("note"));
                        hmCritiques.put(sNote+sComment, true);

                        eCritiques.addContent(eCritique);
                    }
                }

                //Recherche de tous les languages par rapport au film de la variable sTitreSansF, puis résultat sous forme de liste
                sTemp = "/projections/projection/film[attribute::idFilm='" + sTitreSansF + "']/language";
                XPathExpression xpLangage = XPathFac.compile(sTemp);
                List<Element> lLangages = xpLangage.evaluate(docSource);
                HashMap<String, Integer> hmLangages = new HashMap<String, Integer>();

                //Création de la balise langages
                eLangages = new Element("langages");
                StringBuffer sbLangages = new StringBuffer();

                //Parcours des différentes langues
                for(Element langage : lLangages){
                    String sLangue = langage.getAttributeValue("langue");
                    Integer iLangue = Integer.valueOf(langage.getAttributeValue("idLanguage"));
                    //Création d'une string avec les différents id des langues
                    if(hmLangages.get(iLangue) == null){
                        sbLangages.append("L" + iLangue + " ");
                        hmLangages.put(sLangue, iLangue);
                        hmMapLangages.put(iLangue, sLangue);
                        alLangagesI.add(iLangue);
                        alLangagesS.add(sLangue);
                    }
                }

                //Création de l'attribut liste avec la string formée ci-dessus
                eLangages.setAttribute("liste", String.valueOf(sbLangages));
                eFilm.addContent(eLangages);

                //Recherche de tous les genres par rapport au film de la variable sTitreSansF, puis résultat sous forme de liste
                sTemp = "/projections/projection/film[attribute::idFilm='" + sTitreSansF + "']/genre";
                XPathExpression xpGenre = XPathFac.compile(sTemp);
                List<Element> lGenres = xpGenre.evaluate(docSource);
                HashMap<String, Integer> hmGenres = new HashMap<String, Integer>();

                //Création de la balise genres
                eGenres = new Element("genres");

                StringBuffer sbGenres = new StringBuffer();

                //Parcours des différents genres
                for(Element genre : lGenres){
                    String sGenre = genre.getAttributeValue("genreFilm");
                    Integer iGenre = Integer.valueOf(genre.getAttributeValue("idGenre"));
                    //Création d'une string avec les différents id des genres
                    if(hmGenres.get(iGenre) == null){
                        sbGenres.append("G" + iGenre + " ");
                        hmGenres.put(sGenre, iGenre);
                        hmMapGenres.put(iGenre, sGenre);
                        alGenresI.add(iGenre);
                        alGenresS.add(sGenre);
                    }
                }

                //Création de l'attribut liste avec la string formée ci-dessus
                eGenres.setAttribute("liste", String.valueOf(sbGenres));
                eFilm.addContent(eGenres);

                //Recherche de tous les mots cles par rapport au film de la variable sTitreSansF, puis résultat sous forme de liste
                sTemp = "/projections/projection/film[attribute::idFilm='" + sTitreSansF + "']/motCle";
                XPathExpression xpMotCle = XPathFac.compile(sTemp);
                List<Element> lMotCles = xpMotCle.evaluate(docSource);
                HashMap<String, Integer> hmMotCles = new HashMap<String, Integer>();

                //Création de la balise mot_cles
                eMotCles = new Element("motsCles");
                StringBuffer sbMotCles = new StringBuffer();

                //Parcours des différents mots clés
                for(Element motCle : lMotCles){
                    String sMotCle = motCle.getAttributeValue("label");
                    Integer iMotCle = Integer.valueOf(motCle.getAttributeValue("idMotCle"));
                    //Création d'une string avec les différents id des mots clés
                    if(hmMotCles.get(iMotCle) == null){
                        sbMotCles.append("M" + iMotCle + " ");
                        hmMotCles.put(sMotCle, iMotCle);
                        hmMapMotsCles.put(iMotCle, sMotCle);
                        alMotsClesI.add(iMotCle);
                        alMotsClesS.add(sMotCle);
                    }
                }

                //Création de l'attribut liste avec la string formée ci-dessus
                eMotCles.setAttribute("liste", String.valueOf(sbMotCles));
                eFilm.addContent(eMotCles);

                //Création de la balise roles
                eRoles = new Element("roles");
                eFilm.addContent(eRoles);

                //Recherche de tous les roles par rapport au film de la variable sTitreSansF, puis résultat sous forme de liste
                sTemp = "/projections/projection/film[attribute::idFilm='" + sTitreSansF + "']/roles/role";
                XPathExpression xpRole = XPathFac.compile(sTemp);
                List<Element> lRoles = xpRole.evaluate(docSource);
                HashMap<String, Boolean> hmRoles = new HashMap<String, Boolean>();

                //Parcours des différents roles
                for(Element role : lRoles){
                    String sPlace = role.getAttributeValue("place");
                    String sPersonnage = role.getChild("acteur").getAttributeValue("personnage");
                    String sActeur = "A" + role.getChild("acteur").getAttributeValue("idActeur");
                    //Création de ka balise role avec ses différents attributs
                    if(hmRoles.get(sPlace+sPersonnage+sActeur) == null){
                        eRole = new Element("role");
                        eRole.setAttribute("place", sPlace);
                        eRole.setAttribute("personnage", sPersonnage);
                        eRole.setAttribute("idActeur", sActeur);
                        hmRoles.put(sPlace+sPersonnage+sActeur, true);

                        eRoles.addContent(eRole);
                    }
                }

                eFilms.addContent(eFilm);
            }
        }

        //Création de la balise acteurs et ajout dans plex
        eActeurs = new Element("acteurs");
        ePlex.addContent(eActeurs);

        //Parcours des différents films projetés
        for(int i = 0; i < vTitreFilms.size(); ++i){
            //Recherche de tous les acteurs par rapport au film de la variable sTitreSansF, puis résultat sous forme de liste
            String sTemp = "/projections/projection/film[attribute::idFilm='" + vTitreFilms.elementAt(i) + "']/roles/role/acteur";
            XPathExpression xpActeurs = XPathFac.compile(sTemp, Filters.element());
            List<Element> lActeurs = xpActeurs.evaluate(docSource);

            for(int j = 0; j < lActeurs.size(); ++j){
                hmActeurs.put(Integer.valueOf(lActeurs.get(j).getAttributeValue("idActeur")), "temp");
            }

            //Parcours des différents acteurs
            for(Element acteur : lActeurs){

                //Création de la balise acteur avec ses attributs
                if(!hmActeurs.get(Integer.parseInt(acteur.getAttributeValue("idActeur"))).equals("null") &&
                        !hmCheckActeurs.get(Integer.parseInt(acteur.getAttributeValue("idActeur"))).equals("null")){

                    hmActeurs.put(Integer.parseInt(acteur.getAttributeValue("idActeur")), "null");
                    hmCheckActeurs.put(Integer.parseInt(acteur.getAttributeValue("idActeur")), "null");
                    eActeur = new Element("acteur");
                    eActeur.setAttribute("no", "A" + acteur.getAttributeValue("idActeur"));

                    //Création de la balise nom avec son texte
                    eNom = new Element("nom");
                    eNom.setText(acteur.getAttributeValue("nom"));
                    eActeur.addContent(eNom);

                    //Création de la balise nom_naissance avec son texte
                    eNomNaissance = new Element("nom_naissance");
                    eNomNaissance.setText(acteur.getAttributeValue("nomNaissance"));
                    eActeur.addContent(eNomNaissance);

                    //Création de la balise sexe avec son attribut
                    eSexe = new Element("sexe");
                    eSexe.setAttribute("valeur", acteur.getAttributeValue("sexe"));
                    eActeur.addContent(eSexe);

                    //Création de la balise date_naissance avec son attribut et son texte
                    eDateNaissance = new Element("date_naissance");
                    eDateNaissance.setAttribute("format", acteur.getAttributeValue("format"));
                    eDateNaissance.setText(acteur.getAttributeValue("dateNaissance"));
                    eActeur.addContent(eDateNaissance);

                    //Création de la balise date_deces avec son attribut et son texte
                    eDateDeces = new Element("dateDeces");
                    eDateDeces.setAttribute("format", acteur.getAttributeValue("format"));
                    eDateDeces.setText(acteur.getAttributeValue("dateDeces"));
                    eActeur.addContent(eDateDeces);

                    //Création de la balise biographie avec son texte
                    eBiographie = new Element("biographie");
                    eBiographie.setText(acteur.getChild("biographie").getText());
                    eActeur.addContent(eBiographie);

                    eActeurs.addContent(eActeur);
                }
            }
        }

        //Création et ajout de la balise liste_langages
        eListeLangages = new Element("listeLangages");
        //Ajout de liste_langages dans plex
        ePlex.addContent(eListeLangages);

        //Parcours des différents id de langues
        for(int i = 0; i < hmMapLangages.size(); ++i){
            //Création de la balise langage avec son attribut et son texte
            eLangage = new Element("langage");
            eLangage.setAttribute("no", "L" + String.valueOf(alLangagesI.get(i)));
            eLangage.setText(String.valueOf(alLangagesS.get(i)));

            eListeLangages.addContent(eLangage);
        }

        //Création et ajout de la balise liste_genres
        eListeGenres = new Element("listeGenres");
        //Ajout de liste_genres dans plex
        ePlex.addContent(eListeGenres);

        //Parcours des différents id de genres
        for(int i = 0; i < hmMapGenres.size(); ++i){
            //Création de la balise genre avec son attribut et son texte
            eGenre = new Element("genre");
            eGenre.setAttribute("no", "G" + String.valueOf(alGenresI.get(i)));
            eGenre.setText(String.valueOf(alGenresS.get(i)));

            eListeGenres.addContent(eGenre);
        }

        //Création et ajout de la balise liste_mots_cles
        eListeMotsCles = new Element("listeMotsCles");
        //Ajout de liste_mots_cles dans plex
        ePlex.addContent((eListeMotsCles));

        //Parcours des différents id de mots cles
        for(int i = 0; i < hmMapMotsCles.size(); ++i){
            //Création de la balise mot_cle avec son attribut et son texte
            eMotCle = new Element("motCle");
            eMotCle.setAttribute("no", "M" + String.valueOf(alMotsClesI.get(i)));
            eMotCle.setText(String.valueOf(alMotsClesS.get(i)));

            eListeMotsCles.addContent(eMotCle);
        }

        // -- Enregistrer le résultat dans le fichier projections.xml
        enregistrer (document, "projections.xml");

    }

    private static void  enregistrer(Document document, String nomFichier){
        try 	   {
            //Utilisation d'un affichage classique avec getPrettyFormat()
            XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
            //Pour effectuer la sérialisation, il suffit simplement de créer
            // une instance de FileOutputStream en passant le nom du fichier
            sortie.output(document, new FileOutputStream(nomFichier));
        }
        catch (java.io.IOException e){}
    }

}