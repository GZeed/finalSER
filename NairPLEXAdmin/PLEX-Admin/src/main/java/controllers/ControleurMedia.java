package controllers;

import ch.heigvd.iict.ser.imdb.models.Role;
import com.google.gson.*;

import models.*;
import views.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ControleurMedia {

	private ControleurGeneral ctrGeneral;
	private static MainGUI mainGUI;
	private ORMAccess ormAccess;
	private static final String DATE = "dd:mm:yy:hh:mm";

	private GlobalData globalData;

	public ControleurMedia(ControleurGeneral ctrGeneral, MainGUI mainGUI, ORMAccess ormAccess) {
		this.ctrGeneral = ctrGeneral;
		ControleurMedia.mainGUI = mainGUI;
		this.ormAccess = ormAccess;
	}

	public void sendJSONToMedia() {
		new Thread() {
			public void run() {
				mainGUI.setAcknoledgeMessage("Envoi JSON ... WAIT");
				long currentTime = System.currentTimeMillis();
				DateLabelFormatter dateLabelFormatter = new DateLabelFormatter();
				try {
					globalData = ormAccess.GET_GLOBAL_DATA();

					JsonArray rootArray = new JsonArray();
					List<Projection> projectionList = globalData.getProjections();
					for (Projection p : projectionList) {

						// Projection et attributs
						JsonObject projection = new JsonObject();
						projection.addProperty("id", p.getId());

						Calendar calendar = Calendar.getInstance();
						SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE);
						calendar.setTime(p.getDateHeure().getTime());
						dateFormatter.format(calendar.getTime());
						projection.addProperty("dateHeure", dateFormatter.format(calendar.getTime()));

						// Films and attributs
						JsonObject film = new JsonObject();
						film.addProperty("id", p.getFilm().getId());
						film.addProperty("titre", p.getFilm().getTitre());
						projection.add("film", film);

						// Actors and attributs
						JsonArray acteurs = new JsonArray();
						Set<RoleActeur> roleActeurSet = p.getFilm().getRoles();
						Acteur first = null, second = null;

						int place = 1;
						while (second == null) {
							Iterator it = roleActeurSet.iterator();
							while (it.hasNext()) {
								RoleActeur role = ((RoleActeur) it.next());
								if (role.getPlace() == place) {
									if (first == null) {
										first = role.getActeur();
										++place;
										break;
									} else if (second == null) {
										second = role.getActeur();
										break;
									}
								}
							}
							++place;
						}

						JsonObject role1 = new JsonObject();
						role1.addProperty("id", first.getId());
						role1.addProperty("nom", first.getNom());
						acteurs.add(role1);

						JsonObject role2 = new JsonObject();
						role2.addProperty("id", second.getId());
						role2.addProperty("nom", second.getNom());
						acteurs.add(role2);

						projection.add("acteurs", acteurs);

						rootArray.add(projection);
					}

					JsonObject test = new JsonObject();
					test.add("projections", rootArray);

					Gson moteurGson = new GsonBuilder().setPrettyPrinting().create();
					PrintWriter out = new PrintWriter(new FileWriter("SER_Labo2_Projections.json"));
					out.write(moteurGson.toJson(test));
					out.flush();

					System.out.println("Done in [" + displaySeconds(currentTime, System.currentTimeMillis()) + "]");
					mainGUI.setAcknoledgeMessage("Le fichier JSON a été créé en " + displaySeconds(currentTime, System.currentTimeMillis()));
				} catch (Exception e) {
					mainGUI.setErrorMessage("Construction XML impossible", e.toString());
				}
			}
		}.

				start();
	}

	private static final DecimalFormat doubleFormat = new DecimalFormat("#.#");

	private static final String displaySeconds(long start, long end) {
		long diff = Math.abs(end - start);
		double seconds = ((double) diff) / 1000.0;
		return doubleFormat.format(seconds) + " s";
	}
}