package org.foi.nwtis.mdurasek.aplikacija_1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.List;

import org.foi.nwtis.mdurasek.vjezba_03.konfiguracije.Konfiguracija;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.Lokacija;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Klasa za dretve
 * @author NWTiS_3
 *
 */
public class DretvaZahtjeva extends Thread {

	private Socket veza = null;
	private Konfiguracija konfig = null;
	int brojac = 1;
	
	/**
	 * Konstruktor za dretvu
	 * @param veza
	 * @param konf
	 */
	public DretvaZahtjeva(Socket veza, Konfiguracija konf) {
		this.veza = veza;
		this.konfig = konf;
	}

	@Override
	/**
	 * Metoda za pokretanje dretve
	 */
	public synchronized void start() {
		// TODO Auto-generated method stub
		this.setName("mdurasek_"+brojac);
		super.start();
		brojac++;
	}

	@Override
	/**
	 * Metoda za izvršavanje dretve
	 */
	public void run() {
		try (InputStreamReader isr = new InputStreamReader(this.veza.getInputStream(),
				Charset.forName("UTF-8"));
				OutputStreamWriter osw = new OutputStreamWriter(this.veza.getOutputStream(),
						Charset.forName("UTF-8"));) {

			StringBuilder tekst = new StringBuilder();
			while (true) {
				int i = isr.read();
				if (i == -1) {
					break;
				}
				tekst.append((char) i);
			}
			this.veza.shutdownInput();
			String odgovor = obradiNaredbu(tekst);
			osw.write(odgovor);
            osw.flush();
            veza.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Metoda za uplitanje u izvršavanje dretve
	 */
	@Override
	public void interrupt() {
		super.interrupt();
	}

	/**
	 * Selekcija naredbe i prosljeđivanje dalje na obradu metodama
	 * @param tekst
	 * @return
	 */
	private String obradiNaredbu(StringBuilder tekst) {
		String naredba = tekst.toString();
		String odgovor = "";
		if(naredba.equals("STATUS")) {
			odgovor = vratiStatus();
		}
		if(naredba.equals("INIT")) {
			odgovor = vratiInit();
		}
		if(naredba.equals("QUIT")) {
			odgovor = vratiQuit();
		}
		if(naredba.contains("LOAD")) {
			odgovor = vratiLoad();
			if(odgovor.equals("OK")) {
				String splitajNaredbu[] = naredba.split("&&");
				ServerGlavni.aerodromi = new Gson().fromJson(splitajNaredbu[1],new TypeToken<List<Aerodrom>>(){}.getType());
				odgovor = "OK " + ServerGlavni.aerodromi.size();
			}
		}
		if(naredba.contains("DISTANCE")) {
			odgovor = vratiDistance();
			if(odgovor.equals("OK")) {
				String splitajNaredbu[] = naredba.split("&&");
				odgovor = provjeriIcao(ServerGlavni.aerodromi,splitajNaredbu[1],splitajNaredbu[2]);
				if(odgovor.equals("OK")) {
					Double rjesenje = pripremiZaIzracun();
					odgovor = "OK " + rjesenje; 
				}
			}
		}
		if(naredba.equals("CLEAR")) {
			odgovor = vratiClear();
		}
		return odgovor;
	}

	/**
	 * Metoda za vraćanje statusa servera korisniku
	 * @return
	 */
	public String vratiStatus() {
		String odgovor = "";
		String p = Integer.toString(ServerGlavni.port);
		String a = ServerGlavni.adresa;
		if(ServerGlavni.stanjeServera == 0) {
			odgovor = "OK 0"+ "&&" + a + "&&" + p;
		}
		if(ServerGlavni.stanjeServera == 1) {
			odgovor = "OK 1"+ "&&" + a + "&&" + p;
		}
		if(ServerGlavni.stanjeServera == 2) {
			odgovor = "OK 2"+ "&&" + a + "&&" + p;
		}
		return odgovor;
	}
	
	/**
	 * Metoda za inicijalizaciju servera
	 * @return
	 */
	public String vratiInit() {
		String odgovor = "";
		if(ServerGlavni.stanjeServera == 1) {
			odgovor = "ERROR 02 SERVER JE VEĆ INICIJALIZIRAN";
		}
		if(ServerGlavni.stanjeServera == 2) {
			odgovor = "ERROR 03 NIJE MOGUĆE INICIJALIZIRATI AKTIVAN SERVER";
		}
		if(ServerGlavni.stanjeServera == 0) {
			ServerGlavni.stanjeServera = 1;
			odgovor = "OK";
		}
		return odgovor;
	}
	/**
	 * Metoda za aktivaciju servera
	 * @return
	 */
	public String vratiLoad() {
		String odgovor = "";
		if(ServerGlavni.stanjeServera == 0) {
			odgovor = "ERROR 01 SERVER NIJE INICIJALIZIRAN";
		}
		if(ServerGlavni.stanjeServera == 2) {
			odgovor = "ERROR 03 SERVER JE VEĆ AKTIVIRAN";
		}
		if(ServerGlavni.stanjeServera == 1) {
			ServerGlavni.stanjeServera = 2;
			odgovor = "OK";
		}
		return odgovor;
	}
	/**
	 * Metoda za hibernaciju servera
	 * @return
	 */
	public String vratiClear() {
		String odgovor = "";
		if(ServerGlavni.stanjeServera == 0) {
			odgovor = "ERROR 01 SERVER HIBERNIRA";
		}
		if(ServerGlavni.stanjeServera == 1) {
			odgovor = "ERROR 02 SERVER NIJE AKTIVIRAN";
		}
		if(ServerGlavni.stanjeServera == 2) {
			odgovor = "OK";
			ServerGlavni.stanjeServera = 0;
			ServerGlavni.aerodromi.clear();
		}
		return odgovor;
	}
	/**
	 * Metoda za vraćanje distancea
	 * @return
	 */
	public String vratiDistance() {
		String odgovor = "";
		if(ServerGlavni.stanjeServera == 0) {
			odgovor = "ERROR 01 SERVER HIBERNIRA";
		}
		if(ServerGlavni.stanjeServera == 1) {
			odgovor = "ERROR 02 SERVER NIJE AKTIVIRAN";
		}
		if(ServerGlavni.stanjeServera == 2) {
			odgovor = "OK";
		}
		return odgovor;
	}
	/**
	 * Metoda za poziv gašenja servera
	 * @return
	 */
	public String vratiQuit() {
		ServerGlavni.srusiServer();
		String odgovor = "OK";
		return odgovor;
	}
	/**
	 * Provjeravanje u učitanoj listi postoje li aerodromi kojima se želi izračunati udaljenost
	 * @param aerodromi
	 * @param icao1
	 * @param icao2
	 * @return
	 */
	public String provjeriIcao(List<Aerodrom> aerodromi,String icao1,String icao2) {
		String odgovor = "OK";
		int postojiPrvi = 0;
		int postojiDrugi = 0;
		for(Aerodrom a:aerodromi) {
			if(a.getIcao().equals(icao1)) {
				postojiPrvi = 1;
				ServerGlavni.aerodromPrvi.add(a);
				break;
			}
		}
		for(Aerodrom a:aerodromi) {
			if(a.getIcao().equals(icao2)) {
				postojiDrugi = 1;
				ServerGlavni.aerodromDrugi.add(a);
				break;
			}
		}
		if(postojiPrvi == 0 && postojiDrugi == 0) {
			odgovor = "ERROR 13 OBA AERODROMA NE POSTOJE";
		}
		if(postojiPrvi == 1 && postojiDrugi == 0) {
			odgovor = "ERROR 12 DRUGI AERODROM NE POSTOJI";
		}
		if(postojiPrvi == 0 && postojiDrugi == 1) {
			odgovor = "ERROR 11 PRVI AERODROM NE POSTOJI";
		}
		return odgovor;
	}
	/**
	 * Pripremanje podataka za izračun udaljenosti i poziv metoda za izračun
	 * @return
	 */
	private Double pripremiZaIzracun() {
		Lokacija lokPrvi = null;
		Lokacija lokDrugi = null;
		for(Aerodrom a : ServerGlavni.aerodromPrvi) {
			lokPrvi = a.getLokacija();
		}
		for(Aerodrom a: ServerGlavni.aerodromDrugi) {
			lokDrugi = a.getLokacija();
		}
		ServerGlavni.aerodromPrvi.clear();
		ServerGlavni.aerodromDrugi.clear();
		Double sirinaPrvi = Double.parseDouble(lokPrvi.getLatitude());
		Double duzinaPrvi = Double.parseDouble(lokPrvi.getLongitude());
		Double sirinaDrugi = Double.parseDouble(lokDrugi.getLatitude());
		Double duzinaDrugi = Double.parseDouble(lokDrugi.getLongitude());
		Double rjesenje = izracunajUdaljenost(sirinaPrvi, duzinaPrvi, sirinaDrugi, duzinaDrugi);
		return rjesenje;
	}
	
	/**
	 * Računanje udaljenosti između 2 aerodroma
	 * @param icaoPrviSirina
	 * @param icaoPrviDuzina
	 * @param icaoDrugiSirina
	 * @param icaoDrugiDuzina
	 * @return
	 */
	private Double izracunajUdaljenost (Double icaoPrviSirina,Double icaoPrviDuzina,Double icaoDrugiSirina,Double icaoDrugiDuzina) {
		Double sirina = stupnjeviURadijane(icaoDrugiSirina - icaoPrviSirina);
		Double duzina = stupnjeviURadijane(icaoDrugiDuzina - icaoPrviDuzina);
		icaoPrviSirina = stupnjeviURadijane(icaoPrviSirina);
		icaoDrugiSirina = stupnjeviURadijane(icaoDrugiSirina);
		double izracun = Math.sin(sirina / 2) * Math.sin(sirina / 2) +
				   Math.sin(duzina / 2) * Math.sin(duzina / 2) *
				   Math.cos(icaoPrviSirina) * Math.cos(icaoDrugiSirina);
		double rezultat = 6371 * (2 * Math.atan2(Math.sqrt(izracun), Math.sqrt(1 - izracun)));
		return rezultat;
	}
	
	/**
	 * Pretvaranje stupnjeva u radijane
	 * @param stupnjevi
	 * @return
	 */
	private Double stupnjeviURadijane(Double stupnjevi) {
		return stupnjevi * Math.PI / 180 ;
	}
}
