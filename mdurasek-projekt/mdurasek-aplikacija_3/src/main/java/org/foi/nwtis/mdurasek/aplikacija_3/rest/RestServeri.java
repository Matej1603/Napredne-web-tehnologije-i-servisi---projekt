package org.foi.nwtis.mdurasek.aplikacija_3.rest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.foi.nwtis.mdurasek.aplikacija_3.podaci.AerodromDAO;
import org.foi.nwtis.mdurasek.aplikacija_3.podaci.BazaKorisnik;
import org.foi.nwtis.mdurasek.aplikacija_3.podaci.Grupa;
import org.foi.nwtis.mdurasek.aplikacija_3.podaci.Korisnik;
import org.foi.nwtis.mdurasek.aplikacija_3.podaci.KorisnikDAO;
import org.foi.nwtis.mdurasek.aplikacija_3.podaci.Odgovor;
import org.foi.nwtis.mdurasek.aplikacija_3.podaci.Token;

import org.foi.nwtis.mdurasek.aplikacija_3.slusaci.SlusacAplikacije;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.AvionLetiID;
import org.foi.nwtis.rest.podaci.Lokacija;

import com.google.gson.Gson;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
/**
 * Klasa za krajnju točku provjere
 * @author NWTiS_3
 *
 */
@Path("serveri")
public class RestServeri {
	
	/**
	 * Metoda koja šalje status na server glavni
	 * @param korisnik
	 * @param zeton
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	public Response saznajStatus(@HeaderParam("korisnik") String korisnik,@HeaderParam("zeton") int zeton) {
		Response odgovor = null;
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		boolean neispravanKorisnik = BazaKorisnik.pristupTudemTokenu(kon, korisnik, zeton);
		boolean postoji = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnik, zeton);
		if(neispravanKorisnik == false) {
			if(postoji == true) {
					KorisnikGlavni k = new KorisnikGlavni();
					String server = k.posaljiKomandu(kon.dajPostavku("server.glavni.adresa"),Integer.parseInt(kon.dajPostavku("server.glavni.port")),"STATUS");
					if(server == null) {
						odgovor = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Server nije podignut ili ne postoji!").build();
						return odgovor;
					}
					String splitanOdgovor [] = server.split("&&");
					Odgovor novi = new Odgovor(splitanOdgovor[1],Integer.parseInt(splitanOdgovor[2]));
					if(splitanOdgovor[0].equals("OK 0")) {
						odgovor = Response.status(001).entity(novi).build();
					}
					if(splitanOdgovor[0].equals("OK 1")) {
						odgovor = Response.status(002).entity(novi).build();
					}
					if(splitanOdgovor[0].equals("OK 2")) {
						odgovor = Response.status(003).entity(novi).build();
					}
			}
			else {
				odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Vaš token je istekao").build();
			}
		}
		else {
			odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Ne postoji korisnik sa takvim tokenom").build();
		}
		return odgovor;
	}
	
	/**
	 * Metoda koja šalje ostale komanda na server glavni
	 * @param korisnik
	 * @param zeton
	 * @param komanda
	 * @return
	 */
	@GET
	@Path("{komanda}")
	@Produces({ MediaType.APPLICATION_JSON})
	public Response slanjeKomande(@HeaderParam("korisnik") String korisnik,@HeaderParam("zeton") int zeton,@PathParam("komanda")String komanda) {
		Response odgovor = null;
		if(komanda.equals("STATUS")||komanda.equals("LOAD")||komanda.contains("DISTANCE")) {
			odgovor = Response.status(Response.Status.NOT_FOUND).build();
			return odgovor;
			
		}
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		boolean neispravanKorisnik = BazaKorisnik.pristupTudemTokenu(kon, korisnik, zeton);
		boolean postoji = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnik, zeton);
		if(neispravanKorisnik == false) {
			if(postoji == true) {
					KorisnikGlavni k = new KorisnikGlavni();
					String server = k.posaljiKomandu(kon.dajPostavku("server.glavni.adresa"),Integer.parseInt(kon.dajPostavku("server.glavni.port")), komanda);
					if(server == null) {
						odgovor = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Server nije podignut ili ne postoji!").build();
						return odgovor;
					}
					if(server.contains("ERROR")) {
						odgovor = Response.status(Response.Status.BAD_REQUEST).entity(server).build();
					}
					else {
						odgovor = Response.status(Response.Status.OK).entity(server).build();
					}
			}
			else {
				odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Vaš token je istekao").build();
			}
		}
		else {
			odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Ne postoji korisnik sa takvim tokenom").build();
		}
		return odgovor;
	}
	
	/**
	 * Metoda koja šalje listu praćenih aerodroma na server glavni
	 * @param korisnik
	 * @param zeton
	 * @return
	 */
	@POST
	@Path("/LOAD")
	@Produces({ MediaType.APPLICATION_JSON})
	public Response slanjeAerodroma(@HeaderParam("korisnik") String korisnik,@HeaderParam("zeton") int zeton) {
		Response odgovor = null;
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		boolean neispravanKorisnik = BazaKorisnik.pristupTudemTokenu(kon, korisnik, zeton);
		boolean postoji = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnik, zeton);
		if(neispravanKorisnik == false) {
			if(postoji == true) {
					List<Aerodrom> aerodromi = AerodromDAO.dohvatiSvePraceneAerodrome(kon);
					String jsonString = new Gson().toJson(aerodromi);
					KorisnikGlavni k = new KorisnikGlavni();
					String server = k.posaljiKomandu(kon.dajPostavku("server.glavni.adresa"),Integer.parseInt(kon.dajPostavku("server.glavni.port")),"LOAD&&"+jsonString);
					if(server == null) {
						odgovor = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Server nije podignut ili ne postoji!").build();
						return odgovor;
					}
					else {
					odgovor = Response.status(Response.Status.OK).entity(server).build();
					}
			}
			else {
				odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Vaš token je istekao").build();
			}
		}
		else {
			odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Ne postoji korisnik sa takvim tokenom").build();
		}
		return odgovor;
	}
}
