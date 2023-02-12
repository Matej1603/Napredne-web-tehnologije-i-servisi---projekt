package org.foi.nwtis.mdurasek.aplikacija_5.ws;

import java.util.ArrayList;
import java.util.List;

import org.foi.nwtis.mdurasek.aplikacija_5.podaci.ServisKomunikacija;
import org.foi.nwtis.mdurasek.aplikacija_5.slusaci.SlusacAplikacije;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

@WebService(serviceName = "meteo")
/**
 * Klasa za upravljanje servisima meteo
 * @author NWTiS_3
 *
 */
public class WsMeteo {

	@Resource
	private WebServiceContext wsContext;
	

	/**
	 * Web metoda za vraćanje meteo podataka aerodroma
	 * @param korisnik
	 * @param zeton
	 * @param icao
	 * @return
	 */
	@WebMethod
	public MeteoPodaci dajMeteo(@WebParam(name = "korisnik") String korisnik,@WebParam(name = "zeton") String zeton,@WebParam(name = "icao") String icao) {
		KonfiguracijaBP  kon = SlusacAplikacije.posaljiKonfiguraciju();
		Aerodrom aerodrom = null;
		aerodrom = ServisKomunikacija.vratiAerodromZaMeteo(korisnik, zeton, icao);
		System.out.println(aerodrom.getDrzava());
		Lokacija l = aerodrom.getLokacija();
		OWMKlijent owmKlijent = new OWMKlijent(kon.dajPostavku("OpenWeatherMap.apikey"));
		MeteoPodaci mp = null;
		try {
			mp = owmKlijent.getRealTimeWeather(l.getLatitude(), l.getLongitude());
		} catch (NwtisRestIznimka e) {
			e.printStackTrace();
		}
		return mp;
	}
}
