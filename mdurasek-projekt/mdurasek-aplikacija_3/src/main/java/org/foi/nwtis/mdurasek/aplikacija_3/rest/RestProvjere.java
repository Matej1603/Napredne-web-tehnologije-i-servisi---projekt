package org.foi.nwtis.mdurasek.aplikacija_3.rest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.foi.nwtis.mdurasek.aplikacija_3.podaci.AerodromDAO;
import org.foi.nwtis.mdurasek.aplikacija_3.podaci.BazaKorisnik;
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
@Path("provjere")
public class RestProvjere {
	
	/**
	 * Metoda za stvaranje novog žetona
	 * @param korisnik
	 * @param lozinka
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	public Response stvaranjeZetona(@HeaderParam("korisnik") String korisnik,@HeaderParam("lozinka") String lozinka ) {
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		int trajanjeZetona = Integer.parseInt(kon.dajPostavku("zeton.trajanje"));
		Response odgovor = null;
		ArrayList<Token> tokeni = new ArrayList<Token>();
		boolean postoji = BazaKorisnik.provjeriKorisnika(kon, korisnik, lozinka);
		if(postoji == true) {
			tokeni = BazaKorisnik.stvoriZeton(kon, korisnik, trajanjeZetona);
			if(!tokeni.isEmpty()) {
				odgovor = Response.status(Response.Status.OK).entity(tokeni).build();
			}
		}
		else {
			odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Ne postoji takav korisnik").build();
		}
		return odgovor;
	}

	/**
	 * Metoda za provjeru stanja žetona i info o istom
	 * @param korisnik
	 * @param lozinka
	 * @param token
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("{token}")
	public Response provjeriZeton(@HeaderParam("korisnik") String korisnik,@HeaderParam("lozinka") String lozinka,@PathParam("token") int token) {
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		Response odgovor = null;
		boolean postoji = BazaKorisnik.provjeriKorisnika(kon, korisnik, lozinka);
		boolean tudiToken = true;
		boolean aktivanToken = false;
		if(postoji == true) {
				tudiToken = BazaKorisnik.pristupTudemTokenu(kon, korisnik, token);
				if(tudiToken == false) {
					aktivanToken = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnik, token);
					if(aktivanToken == true) {
						odgovor = Response.status(Response.Status.OK).entity("Token je važeći").build();
					}
					else {
						odgovor = Response.status(Response.Status.REQUEST_TIMEOUT).entity("Token je istekao").build();
					}
				}
				else {
					odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Ovo nije vaš token").build();
				}
		}
		else {
			odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Ne postoji takav korisnik").build();
		}
		return odgovor;
	}
	/**
	 * Metoda za brisanje žetona
	 * @param korisnik
	 * @param lozinka
	 * @param token
	 * @return
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("{token}")
	public Response obrisiZeton(@HeaderParam("korisnik") String korisnik,@HeaderParam("lozinka") String lozinka,@PathParam("token") int token) {
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		Response odgovor = null;
		boolean postoji = BazaKorisnik.provjeriKorisnika(kon, korisnik, lozinka);
		boolean tudiToken = true;
		boolean aktivanToken = false;
		if(postoji == true) {
			tudiToken = BazaKorisnik.pristupTudemTokenu(kon, korisnik, token);
			if(tudiToken == false) {
				aktivanToken = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnik, token);
				if(aktivanToken == true) {
					BazaKorisnik.deaktivirajToken(kon, token);
					odgovor = Response.status(Response.Status.OK).entity("Token je uspješno obrisan").build();
				}
				else {
					odgovor = Response.status(Response.Status.REQUEST_TIMEOUT).entity("Token je istekao").build();
				}
			}
			else {
				odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Ovo nije vaš token").build();
			}
		}
		else {
			odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Ne postoji takav korisnik").build();
		}
		return odgovor;
	}
	
	/**
	 * Metoda za brisanje svih žetona korisnika
	 * @param korisnikH
	 * @param lozinka
	 * @param korisnik
	 * @return
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("korisnik/{korisnik}")
	public Response obrisiSveZetoneKorisnika(@HeaderParam("korisnik") String korisnikH,@HeaderParam("lozinka") String lozinka,@PathParam("korisnik") String korisnik) {
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		Response odgovor = null;
		boolean postoji = BazaKorisnik.provjeriKorisnika(kon, korisnikH, lozinka);
		boolean aktivanToken = false;
		String grupa = kon.dajPostavku("sustav.administratori");
		boolean autorizacija = false;
		if(postoji == true) {
			aktivanToken = BazaKorisnik.provjeraBaremJednogAktivnogTokena(kon, korisnikH);
			if(aktivanToken == true) {
				if(korisnik.equals(korisnikH)) {
					BazaKorisnik.deaktivirajSveTokeneKorisnika(kon, korisnik);
					odgovor = Response.status(Response.Status.OK).entity("Tokeni su uspješno obrisani").build();
				}
				else {
					autorizacija = BazaKorisnik.provjeraAutorizacije(kon, korisnikH, grupa);
					if(autorizacija == true) {
						BazaKorisnik.deaktivirajSveTokeneKorisnika(kon, korisnik);
						odgovor = Response.status(Response.Status.OK).entity("Tokeni su uspješno obrisani").build();
					}
					else {
						odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Nemate ovlasti brisanja tuđih tokena").build();
					}
				}
			}
			else {
				odgovor = Response.status(Response.Status.NOT_FOUND).entity("Nemate nijedan aktivan token").build();
			}
		}
		else {
			odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Ne postoji takav korisnik").build();
		}
		return odgovor;
	}
}
