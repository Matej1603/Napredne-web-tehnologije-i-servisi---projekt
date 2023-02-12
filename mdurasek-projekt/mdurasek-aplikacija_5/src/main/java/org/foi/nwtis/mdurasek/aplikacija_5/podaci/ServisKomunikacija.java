package org.foi.nwtis.mdurasek.aplikacija_5.podaci;


import java.util.ArrayList;
import java.util.Arrays;
import org.foi.nwtis.mdurasek.aplikacija_5.slusaci.SlusacAplikacije;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLetiID;
import com.google.gson.Gson;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;


public class ServisKomunikacija {
	
	/**
	 * Pozivanje servisa za vraćanje polazaka s aerodroma u određenom intervalu
	 * @param korisnik
	 * @param zeton
	 * @param vrsta
	 * @param icao
	 * @param odDatuma
	 * @param doDatuma
	 * @return
	 */
	public static ArrayList<AvionLetiID> vratiPolaske(String korisnik, String zeton, String vrsta, String icao,
			String odDatuma, String doDatuma) {
		int zetonInt = Integer.valueOf(zeton);
		KonfiguracijaBP konf = SlusacAplikacije.posaljiKonfiguraciju();
		String adresaWa = konf.dajPostavku("adresa.wa_1");
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target(adresaWa + "/aerodromi/" + icao + "/polasci").queryParam("vrsta", vrsta)
				.queryParam("od", odDatuma).queryParam("do", doDatuma);
		Response restOdgovor = webResource.request().header("korisnik", korisnik).header("zeton", zetonInt).get();
		ArrayList<AvionLetiID> avioni = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			avioni = new ArrayList<>();
			avioni.addAll(Arrays.asList(gson.fromJson(odgovor, AvionLetiID[].class)));
		}
		return avioni;
	}
	
	/**
	 * Pozivanje servisa koji dodaje novi aerodrom u praćene
	 * @param korisnik
	 * @param zeton
	 * @param icao
	 * @return
	 */
	public static boolean dodajUPracene(String korisnik, String zeton,String icao) {
		int zetonInt = Integer.valueOf(zeton);
		KonfiguracijaBP konf = SlusacAplikacije.posaljiKonfiguraciju();
		String adresaWa = konf.dajPostavku("adresa.wa_1");
		Client client = ClientBuilder.newClient();
		WebTarget webResourceVratiAerordrom = client.target(adresaWa + "/aerodromi/" + icao );
		Response restOdgovorVratiAerodrom = webResourceVratiAerordrom.request().header("korisnik", korisnik).header("zeton", zetonInt).get();
		Aerodrom novi = new Aerodrom();
		if(restOdgovorVratiAerodrom.getStatus() == 200) {
			novi = restOdgovorVratiAerodrom.readEntity(Aerodrom.class);
			WebTarget webResource = client.target(adresaWa + "/aerodromi/");
			Response restOdgovor = webResource.request().header("korisnik", korisnik).header("zeton", zetonInt).post(Entity.json(novi));
			if(restOdgovor.getStatus() == 200) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Metoda koja vraća jedan aerodrom kao objekt kako bi ga mogao koristit meteo WS
	 * @param korisnik
	 * @param zeton
	 * @param icao
	 * @return
	 */
	public static Aerodrom vratiAerodromZaMeteo(String korisnik,String zeton,String icao) {
		int zetonInt = Integer.valueOf(zeton);
		KonfiguracijaBP konf = SlusacAplikacije.posaljiKonfiguraciju();
		String adresaWa = konf.dajPostavku("adresa.wa_1");
		Client client = ClientBuilder.newClient();
		WebTarget webResourceVratiAerordrom = client.target(adresaWa + "/aerodromi/" + icao );
		Response restOdgovorVratiAerodrom = webResourceVratiAerordrom.request().header("korisnik", korisnik).header("zeton", zetonInt).get();
		Aerodrom novi = new Aerodrom();
		if(restOdgovorVratiAerodrom.getStatus() == 200) {
			novi = restOdgovorVratiAerodrom.readEntity(Aerodrom.class);
			return novi;
		}
		return null;
	}
}
