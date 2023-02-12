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
import org.foi.nwtis.mdurasek.aplikacija_3.podaci.Token;

import org.foi.nwtis.mdurasek.aplikacija_3.slusaci.SlusacAplikacije;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.AvionLetiID;
import org.foi.nwtis.rest.podaci.Lokacija;

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
@Path("korisnici")
public class RestKorisnici {
	/**
	 * Metoda koja vraća sve korisnika
	 * @param korisnik
	 * @param zeton
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	public Response vratiKorisnike(@HeaderParam("korisnik") String korisnik,@HeaderParam("zeton") int zeton) {
		Response odgovor = null;
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		boolean neispravanKorisnik = BazaKorisnik.pristupTudemTokenu(kon, korisnik, zeton);
		boolean postoji = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnik, zeton);
		List<Korisnik> korisnici = new ArrayList<>();
		if(neispravanKorisnik == false) {
			if(postoji == true) {
				korisnici = KorisnikDAO.dohvatiSveKorisnike(kon);
				odgovor = Response.status(Response.Status.OK).entity(korisnici).build();
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
	 * Metoda koja vraća jednog korisnika
	 * @param korisnikH
	 * @param zeton
	 * @param korisnik
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("{korisnik}")
	public Response vratiKorisnika(@HeaderParam("korisnik") String korisnikH,@HeaderParam("zeton") int zeton,@PathParam("korisnik")String korisnik) {
		Response odgovor = null;
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		boolean neispravanKorisnik = BazaKorisnik.pristupTudemTokenu(kon, korisnikH, zeton);
		boolean postoji = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnikH, zeton);
		List<Korisnik> korisnici = new ArrayList<>();
		if(neispravanKorisnik == false) {
			if(postoji == true) {
				korisnici = KorisnikDAO.dohvatiKorisnika(kon,korisnik);
				odgovor = Response.status(Response.Status.OK).entity(korisnici).build();
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

	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("{korisnik}/grupe")
	public Response vratiGrupeKorisnika(@HeaderParam("korisnik") String korisnikH,@HeaderParam("zeton") int zeton,@PathParam("korisnik")String korisnik) {
		Response odgovor = null;
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		boolean neispravanKorisnik = BazaKorisnik.pristupTudemTokenu(kon, korisnikH, zeton);
		boolean postoji = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnikH, zeton);
		List<Grupa> grupe = new ArrayList<>();
		if(neispravanKorisnik == false) {
			if(postoji == true) {
				grupe = KorisnikDAO.vratiGrupeKorisnika(kon, korisnik);
				odgovor = Response.status(Response.Status.OK).entity(grupe).build();
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
	 * Metoda koja dodaje novog korisnika
	 * @param korisnikH
	 * @param zeton
	 * @param novi
	 * @return
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response dodajKorisnika(@HeaderParam("korisnik") String korisnikH, @HeaderParam("zeton") int zeton,
			Korisnik novi) {
		Response odgovor = null;
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		boolean neispravanKorisnik = BazaKorisnik.pristupTudemTokenu(kon, korisnikH, zeton);
		boolean postoji = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnikH, zeton);
		if (neispravanKorisnik == false) {
			if (postoji == true) {
				boolean uspjeh = KorisnikDAO.dodajKorisnika(novi, kon);
				if (uspjeh == true) {
					odgovor = Response.status(Response.Status.OK).entity("Uspješno dodan korisnik").build();
				} 
				else {
					odgovor = Response.status(Response.Status.NOT_FOUND).entity("Ups, dogodila se greška").build();
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
