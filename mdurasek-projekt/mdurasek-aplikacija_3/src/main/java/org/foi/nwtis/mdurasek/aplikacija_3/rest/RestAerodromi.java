package org.foi.nwtis.mdurasek.aplikacija_3.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.foi.nwtis.mdurasek.aplikacija_3.podaci.AerodromDAO;
import org.foi.nwtis.mdurasek.aplikacija_3.podaci.BazaKorisnik;
import org.foi.nwtis.mdurasek.aplikacija_3.podaci.Grupa;
import org.foi.nwtis.mdurasek.aplikacija_3.podaci.Korisnik;
import org.foi.nwtis.mdurasek.aplikacija_3.podaci.KorisnikDAO;
import org.foi.nwtis.mdurasek.aplikacija_3.slusaci.SlusacAplikacije;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.AvionLetiID;
import org.foi.nwtis.rest.podaci.Lokacija;

import com.google.gson.Gson;

import jakarta.ws.rs.Consumes;
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
 * Klasa za krajnju točku aerodromi
 * @author NWTiS_3
 *
 */
@Path("aerodromi")
public class RestAerodromi {
	/**
	 * Metoda koja vraća sve aerodrome
	 * @param preuzimanje
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dajSveAerodrome(@HeaderParam("korisnik") String korisnik,@HeaderParam("zeton") int zeton) {
		Response odgovor = null;
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		boolean neispravanKorisnik = BazaKorisnik.pristupTudemTokenu(kon, korisnik, zeton);
		boolean postoji = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnik, zeton);
		List<Aerodrom> aerodromi = new ArrayList<>();
		if(neispravanKorisnik == false) {
			if(postoji == true) {
				aerodromi = AerodromDAO.dohvatiSveAerodrome(kon);
				odgovor = Response.status(Response.Status.OK).entity(aerodromi).build();
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
	 * Metoda koja vraća jedan aerodrom
	 * @param korisnik
	 * @param zeton
	 * @param icao
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}")
	public Response dajAerodrom(@HeaderParam("korisnik") String korisnik,@HeaderParam("zeton") int zeton,@PathParam("icao") String icao) {
		Response odgovor = null;
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		boolean neispravanKorisnik = BazaKorisnik.pristupTudemTokenu(kon, korisnik, zeton);
		boolean postoji = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnik, zeton);
		List<Aerodrom> aerodromi = new ArrayList<>();
		if(neispravanKorisnik == false) {
			if(postoji == true) {
				aerodromi = AerodromDAO.dohvatiSveAerodrome(kon);
				for(Aerodrom a : aerodromi) {
					if(a.getIcao().compareTo(icao) == 0) {
						odgovor = Response.status(Response.Status.OK).entity(a).build();
						break;
					}
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
	 * Metoda koja vraća udaljenost dva aerodroma
	 * @param korisnik
	 * @param zeton
	 * @param icao1
	 * @param icao2
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao1}/{icao2}")
	public Response dajUdaljenost(@HeaderParam("korisnik") String korisnik,@HeaderParam("zeton") int zeton,@PathParam("icao1") String icao1,@PathParam("icao2")String icao2) {
		Response odgovor = null;
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		boolean neispravanKorisnik = BazaKorisnik.pristupTudemTokenu(kon, korisnik, zeton);
		boolean postoji = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnik, zeton);
		if(neispravanKorisnik == false) {
			if(postoji == true) {
				KorisnikGlavni k = new KorisnikGlavni();
				String server = k.posaljiKomandu(kon.dajPostavku("server.glavni.adresa"),Integer.parseInt(kon.dajPostavku("server.glavni.port")),"DISTANCE&&"+icao1+"&&"+icao2);
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
	
	/**
	 * Metoda putem koje dodajem aerodrom u preuzimanja
	 * @param korisnik
	 * @param zeton
	 * @param novi
	 * @return
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response dodajAerodromZaPracenje(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") int zeton,
			Aerodrom novi) {
		Response odgovor = null;
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		boolean neispravanKorisnik = BazaKorisnik.pristupTudemTokenu(kon, korisnik, zeton);
		boolean postoji = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnik, zeton);
		if (neispravanKorisnik == false) {
			if (postoji == true) {
				boolean postojiIcao = AerodromDAO.provjeriIcaoUPracenima(kon, novi.getIcao());
				if (postojiIcao == false) {
					boolean uspjeh = AerodromDAO.dodajAerodromZaPracenje(kon, novi.getIcao());
					if(uspjeh == true) {
						odgovor = Response.status(Response.Status.OK).entity("Uspješno dodan aerodrom u praćene").build();
					}
					else {
						odgovor = Response.status(Response.Status.NOT_FOUND).entity("Ups, dogodila se neočekivana pogreška").build();
					}
				} 
				else {
					odgovor = Response.status(Response.Status.NOT_FOUND).entity("Ups, ovaj aerodrom je već dodan u praćene").build();
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
	 * Metoda koja vraća polaske za icao
	 * @param korisnik
	 * @param zeton
	 * @param icao
	 * @param x
	 * @param vrijemeOd
	 * @param vrijemeDo
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}/polasci")
	public Response dajPolaskeAerodoma(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") int zeton,@PathParam("icao")String icao,@QueryParam("vrsta")String x,@QueryParam("od")String vrijemeOd,@QueryParam("do")String vrijemeDo) {
		Response odgovor = null;
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		boolean neispravanKorisnik = BazaKorisnik.pristupTudemTokenu(kon, korisnik, zeton);
		boolean postoji = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnik, zeton);
		ArrayList<AvionLetiID> avioni = new ArrayList<AvionLetiID>();
		if(neispravanKorisnik == false) {
			if(postoji == true) {
				avioni = AerodromDAO.dohvatiPolaskeZaIcaoNaDatum(kon, icao,vrijemeOd, vrijemeDo,x);
				odgovor = Response.status(Response.Status.OK).entity(avioni).build();
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
	 * Metoda koja vraća dolaske za icao
	 * @param korisnik
	 * @param zeton
	 * @param icao
	 * @param x
	 * @param vrijemeOd
	 * @param vrijemeDo
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}/dolasci")
	public Response dajDolaskeAerodoma(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") int zeton,@PathParam("icao")String icao,@QueryParam("vrsta")String x,@QueryParam("od")String vrijemeOd,@QueryParam("do")String vrijemeDo) {
		Response odgovor = null;
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		BazaKorisnik.osvjeziTablicuTokena(kon);
		boolean neispravanKorisnik = BazaKorisnik.pristupTudemTokenu(kon, korisnik, zeton);
		boolean postoji = BazaKorisnik.vratiStanjeTokenaKorisniku(kon, korisnik, zeton);
		ArrayList<AvionLetiID> avioni = new ArrayList<AvionLetiID>();
		if(neispravanKorisnik == false) {
			if(postoji == true) {
				avioni = AerodromDAO.dohvatiDolaskeZaIcaoNaDatum(kon, icao,vrijemeOd, vrijemeDo,x);
				odgovor = Response.status(Response.Status.OK).entity(avioni).build();
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
	 * Metoda koja se ne traži u projektu ali je potrebna u radu kod 4 aplikacije, vraća sve aerodrome koji su u praćenima
	 * @return
	 */
	@GET
	@Path("/load")
	@Produces({ MediaType.APPLICATION_JSON})
	public Response slanjePracenihAerodroma() {
		Response odgovor = null;
		KonfiguracijaBP kon;
		kon = SlusacAplikacije.posaljiKonfiguraciju();
		List<Aerodrom> aerodromi = AerodromDAO.dohvatiSvePraceneAerodrome(kon);
		String jsonString = new Gson().toJson(aerodromi);
		odgovor = Response.status(Response.Status.OK).entity(jsonString).build();
		return odgovor;
	}
}
