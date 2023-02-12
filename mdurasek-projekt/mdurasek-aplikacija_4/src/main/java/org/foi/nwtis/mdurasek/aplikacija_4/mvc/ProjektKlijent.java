package org.foi.nwtis.mdurasek.aplikacija_4.mvc;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.foi.nwtis.mdurasek.aplikacija_4.podaci.Korisnik;
import org.foi.nwtis.mdurasek.aplikacija_4.podaci.KorisnikGlavni;
import org.foi.nwtis.mdurasek.aplikacija_4.podaci.Token;
import org.foi.nwtis.mdurasek.aplikacija_4.slusaci.SlusacAplikacije;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * Klasa za komunikaciju sa servisima
 * @author NWTiS_3
 *
 */
public class ProjektKlijent {
	
	public static int zeton = 0;
	public static String prijavljeniKorisnik = "";
	
	/**
	 * Pozivanje servisa provjere za stvaranje žetona (prijava)
	 * @param korisnik
	 * @param lozinka
	 * @return
	 */
	public String prijavaKorisnika(String korisnik,String lozinka) {
		String odgovorServisa = "";
		KonfiguracijaBP konf = SlusacAplikacije.posaljiKonfiguraciju();
		String adresaWa = konf.dajPostavku("adresa.wa_1");
		Client client = ClientBuilder.newClient();
		WebTarget webResource = 
				client.target(adresaWa+"/provjere");
		Response restOdgovor = webResource.request()
				.header("korisnik",korisnik).header("lozinka", lozinka)
				.get();
		List<Token> tokeni = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			tokeni = new ArrayList<>();
			tokeni.addAll(Arrays.asList(gson.fromJson(odgovor, Token[].class)));
			for(Token t: tokeni) {
				zeton = t.getZeton();
				prijavljeniKorisnik = korisnik;
				break;
			}
			odgovorServisa = "Uspješna prijava!";
			return odgovorServisa;
		}
		else {
			odgovorServisa = "Prijava je neuspješna pokušajte ponovo!";
			return odgovorServisa;
		}
	}
	
	/**
	 * Pozivanje servisa za provjeru stvaranje žetona kako bi se tim žetonom mogao pozvati servis za dodavanje novog korisnika
	 */
	public String registracijaKorisnika(String korisnik,String lozinka,String prezime,String ime,String email) {
		int zetonZaRegistraciju = 0;
		String odgovorServisa = "Neuspješna registracija!";
		KonfiguracijaBP konf = SlusacAplikacije.posaljiKonfiguraciju();
		String adresaWa = konf.dajPostavku("adresa.wa_1");
		Client client = ClientBuilder.newClient();
		WebTarget webResource = 
				client.target(adresaWa+"/provjere");
		Response restOdgovor = webResource.request()
				.header("korisnik",konf.dajPostavku("korisnik")).header("lozinka", konf.dajPostavku("lozinka"))
				.get();
		List<Token> tokeni = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			tokeni = new ArrayList<>();
			tokeni.addAll(Arrays.asList(gson.fromJson(odgovor, Token[].class)));
			for(Token t: tokeni) {
				zetonZaRegistraciju = t.getZeton();
				break;
			}
		}
		WebTarget webResourceReg = 
				client.target(adresaWa+"/korisnici");
		Korisnik novi = new Korisnik(korisnik,lozinka,prezime,ime,email);
		Response restOdgovorReg = webResourceReg.request()
				.header("korisnik",konf.dajPostavku("korisnik")).header("zeton", zetonZaRegistraciju).post(Entity.json(novi));
		if (restOdgovorReg.getStatus() == 200) {
			odgovorServisa = "Uspješna registracija!";
			return odgovorServisa;
		}
		return odgovorServisa;
	}
	
	/**
	 * Pozivanje servisa za brisanje trenutnog žetona
	 */
	public void obrisiToken() {
		KonfiguracijaBP konf = SlusacAplikacije.posaljiKonfiguraciju();
		String adresaWa = konf.dajPostavku("adresa.wa_1");
		Client client = ClientBuilder.newClient();
		WebTarget webResource = 
				client.target(adresaWa+"/provjere").path(Integer.toString(zeton));
		Response restOdgovor = webResource.request()
				.header("korisnik",konf.dajPostavku("korisnik")).header("lozinka", konf.dajPostavku("lozinka"))
				.delete();
	}
	
	/**
	 * Pozivanje servisa za vraćanje svih korisnika
	 * @return
	 */
	public List<Korisnik> dajSveKorisnike(){
		KonfiguracijaBP konf = SlusacAplikacije.posaljiKonfiguraciju();
		String adresaWa = konf.dajPostavku("adresa.wa_1");
		Client client = ClientBuilder.newClient();
		WebTarget webResource = 
				client.target(adresaWa+"/korisnici");
		Response restOdgovor = webResource.request()
				.header("korisnik",prijavljeniKorisnik).header("zeton", zeton)
				.get();
		List<Korisnik> korisnici = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			korisnici = new ArrayList<>();
			korisnici.addAll(Arrays.asList(gson.fromJson(odgovor, Korisnik[].class)));
			return korisnici;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Komunikacija direkt za aplikacijom_1 te slanje zahtjeva status
	 * @return
	 */
	public String vratiStatus() {
		String odgovor = "";
		KonfiguracijaBP konf = SlusacAplikacije.posaljiKonfiguraciju();
		KorisnikGlavni k = new KorisnikGlavni();
		odgovor = k.posaljiKomandu(konf.dajPostavku("server.glavni.adresa"),Integer.parseInt(konf.dajPostavku("server.glavni.port")),"STATUS");
		if(odgovor == null) {
			odgovor = "Server je ugašen!";
			return odgovor;
		}
		String obradiOdgovor [] = odgovor.split("&&");
		odgovor = obradiOdgovor[0];
		return odgovor;
	}
	
	/**
	 * Slanje ostalih naredbi na server na aplikaciji_1
	 * @param naredba
	 * @return
	 */
	public String obradiNaredbu(String naredba) {
		String odgovor = "";
		KonfiguracijaBP konf = SlusacAplikacije.posaljiKonfiguraciju();
		KorisnikGlavni k = new KorisnikGlavni();
		System.out.println(naredba);
		if(!naredba.equals("LOAD")) {
			odgovor = k.posaljiKomandu(konf.dajPostavku("server.glavni.adresa"),Integer.parseInt(konf.dajPostavku("server.glavni.port")),naredba);
			if(odgovor == null) {
				odgovor = "ERROR Server je ugašen!";
				return odgovor;
			}
		}
		else {
			String adresaWa = konf.dajPostavku("adresa.wa_1");
			Client client = ClientBuilder.newClient();
			WebTarget webResource = 
					client.target(adresaWa+"/aerodromi/load");
			Response restOdgovor = webResource.request().get();
			String aerodromiZaSlat = restOdgovor.readEntity(String.class);
			odgovor = k.posaljiKomandu(konf.dajPostavku("server.glavni.adresa"),Integer.parseInt(konf.dajPostavku("server.glavni.port")),naredba+"&&"+aerodromiZaSlat);
			if(odgovor == null) {
				odgovor = "ERROR Server je ugašen!";
				return odgovor;
			}
		}
		return odgovor;
	}
}
