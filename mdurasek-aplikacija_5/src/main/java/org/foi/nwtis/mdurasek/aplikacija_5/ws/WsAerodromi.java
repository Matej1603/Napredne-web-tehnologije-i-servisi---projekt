package org.foi.nwtis.mdurasek.aplikacija_5.ws;

import java.util.ArrayList;
import java.util.List;

import org.foi.nwtis.mdurasek.aplikacija_5.podaci.ServisKomunikacija;
import org.foi.nwtis.mdurasek.aplikacija_5.slusaci.SlusacAplikacije;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLetiID;
import org.foi.nwtis.rest.podaci.Lokacija;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

@WebService(serviceName = "aerodromi")
/**
 * Klasa za upravaljanje web servisima aerodromi
 * @author NWTiS_3
 *
 */
public class WsAerodromi {

	@Resource
	private WebServiceContext wsContext;
	

	/**
	 * Web metoda za vraćanje polazaka s aerodroma od datuma do datuma
	 * @param korisnik
	 * @param zeton
	 * @param icao
	 * @param danOd
	 * @param danDo
	 * @return
	 */
	@WebMethod
	public List<AvionLetiID> dajPolaskeDan(@WebParam(name = "korisnik")String korisnik,@WebParam(name = "zeton")String zeton,@WebParam(name = "icao")String icao,@WebParam(name = "danOd")String danOd,@WebParam(name = "danDo")String danDo){
		String vrsta = "0";
		List<AvionLetiID> avioni = new ArrayList<>();
		avioni = ServisKomunikacija.vratiPolaske(korisnik, zeton, vrsta, icao, danOd, danDo);
		return avioni;
	}
	
	/**
	 * Web metoda za vraćanje polazaka s aerodroma od epocha do epocha
	 * @param korisnik
	 * @param zeton
	 * @param icao
	 * @param vrijemeOd
	 * @param vrijemeDo
	 * @return
	 */
	@WebMethod
	public List<AvionLetiID> dajPolaskeVrijeme(@WebParam(name = "korisnik")String korisnik,@WebParam(name = "zeton")String zeton,@WebParam(name = "icao")String icao,@WebParam(name = "vrijemeOd")String vrijemeOd,@WebParam(name = "vrijemeDo")String vrijemeDo){
		String vrsta = "1";
		List<AvionLetiID> avioni = new ArrayList<>();
		avioni = ServisKomunikacija.vratiPolaske(korisnik, zeton, vrsta, icao, vrijemeOd, vrijemeDo);
		return avioni;
	}
	/**
	 * Web metoda za dodavanje novog aerodroma u praćene
	 * @param korisnik
	 * @param zeton
	 * @param icao
	 * @return
	 */
	@WebMethod
	public boolean dodajAerodromPreuzimanje(@WebParam(name = "korisnik")String korisnik,@WebParam(name = "zeton")String zeton,@WebParam(name = "icao")String icao) {
		boolean uspjeh = ServisKomunikacija.dodajUPracene(korisnik, zeton, icao);
		return uspjeh;
	}
}
