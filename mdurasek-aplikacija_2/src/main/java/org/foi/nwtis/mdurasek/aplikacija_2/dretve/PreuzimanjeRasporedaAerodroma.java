package org.foi.nwtis.mdurasek.aplikacija_2.dretve;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.foi.nwtis.mdurasek.aplikacija_2.podaci.BazaAerodromi;
import org.foi.nwtis.mdurasek.aplikacija_2.slusaci.SlusacAplikacije;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;

import com.oracle.wls.shaded.org.apache.bcel.generic.AALOAD;

/**
 * Klasa za preuzimanje rasporeda aerodroma
 * @author NWTiS_3
 *
 */

public class PreuzimanjeRasporedaAerodroma extends Thread {
	private String preuzimanjeOd;
	private String preuzimanjeDo;
	private int preuzimanjeVrijeme;
	private int vrijemePauza;
	private int vrijemeCiklusa;
	private long vrijemeObrade;
	private String korime;
	private String lozinka;
	private OSKlijent osKlijent;
	private LocalDateTime dateTimeOd;
	private LocalDateTime dateTimeDo;
	private long dateTimeOdEpoch;
	private long dateTimeDoEpoch;
	private long virtualniBrojac = 0;
	private long stvarniBrojac = 0;
	private KonfiguracijaBP kon;
	private long vrijemeAplikacije;
	private long korekcijaVremena;
	private long prvoVrijemeAplikacije;
	private long odmakUPreuzimanju;
	
	/**
	 * Metoda za pokretanje dretve
	 */
	@Override
	public synchronized void start() {
		kon = SlusacAplikacije.posaljiKonfiguracijuDretvi();
		ucitavanjeKonfigPodataka();
		super.start();
	}
	
	/**
	 * Metoda za preuzimanje podataka iz konfiguracijske datoteke
	 */
	private void ucitavanjeKonfigPodataka() {
		this.preuzimanjeOd = kon.dajPostavku("preuzimanje.od");
		this.preuzimanjeDo = kon.dajPostavku("preuzimanje.do");
		this.preuzimanjeOd = this.preuzimanjeOd + " 00:00";
		this.preuzimanjeDo = this.preuzimanjeDo + " 00:00";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
		this.dateTimeOd = LocalDateTime.parse(this.preuzimanjeOd, formatter);
		this.dateTimeDo = LocalDateTime.parse(this.preuzimanjeDo, formatter);
		this.dateTimeOdEpoch = this.dateTimeOd.toEpochSecond(ZoneOffset.UTC);
		this.dateTimeDoEpoch = this.dateTimeDo.toEpochSecond(ZoneOffset.UTC);
		this.preuzimanjeVrijeme = Integer.parseInt(kon.dajPostavku("preuzimanje.vrijeme"));
		this.vrijemePauza = Integer.parseInt(kon.dajPostavku("preuzimanje.pauza"));
		this.vrijemeCiklusa = Integer.parseInt(kon.dajPostavku("ciklus.vrijeme"));
		this.vrijemeObrade = this.dateTimeOdEpoch;
		this.korekcijaVremena = Integer.parseInt(kon.dajPostavku("ciklus.korekcija"));
		this.korime = kon.dajPostavku("OpenSkyNetwork.korisnik");
		this.lozinka = kon.dajPostavku("OpenSkyNetwork.lozinka");
		this.odmakUPreuzimanju = Integer.parseInt(kon.dajPostavku("preuzimanje.odmak"));
		this.osKlijent = new OSKlijent(korime, lozinka);
	}

	
	/**
	 * Metoda za izvršavanje rada dretve
	 */
	@Override
	public void run() {
		while(vrijemeObrade < dateTimeDoEpoch) {
		vrijemeObrade += 3600 * preuzimanjeVrijeme;
		if(stvarniBrojac == 0) {
			prvoVrijemeAplikacije = System.currentTimeMillis();
			vrijemeAplikacije = prvoVrijemeAplikacije;
		}
		else {
			vrijemeAplikacije += (vrijemeCiklusa * 1000);
		}
		spavanjeDretveDan(dateTimeOdEpoch,vrijemeAplikacije,odmakUPreuzimanju);
		korekcija(stvarniBrojac);
		List<String> aerodromi = BazaAerodromi.dohvatiSvePraceneAerodrome(kon);
		for (String a : aerodromi) {
			List<AvionLeti> avioniPolasci;
			try {
				avioniPolasci = osKlijent.getDepartures(a, dateTimeOdEpoch, vrijemeObrade);
				if (avioniPolasci != null) {
					for (AvionLeti avion : avioniPolasci) {
						
						if(avion.getEstArrivalAirport() == null) {
							BazaAerodromi.dodajUAerodromProblemiZaPolaske(avion, kon);
						}
						else {
							boolean postoji = BazaAerodromi.provjeriKljuceveZaPolaske(kon, avion.getFirstSeen(), avion.getIcao24());
							if(postoji == false) {
								BazaAerodromi.dodajUAerodromPolasci(avion, kon);
							}
						}
					}
				}
			} catch (NwtisRestIznimka e) {
				System.out.println("POLASCI - Nema podataka za: " + a);
				BazaAerodromi.dodajUAerodromProblemiIznimkePolasci(a, kon);
			}
			
			List<AvionLeti> avioniDolasci;
			try {
				avioniDolasci = osKlijent.getArrivals(a, dateTimeOdEpoch, vrijemeObrade);
				if (avioniDolasci != null) {
					for (AvionLeti avion : avioniDolasci) {
						if(avion.getEstDepartureAirport() == null) {
							BazaAerodromi.dodajUAerodromProblemiZaDolaske(avion, kon);
						}
						else {
							boolean postoji = BazaAerodromi.provjeriKljuceveZaDolaske(kon, avion.getFirstSeen(), avion.getIcao24());
							if(postoji == false) {
								BazaAerodromi.dodajUAerodromDolasci(avion, kon);
							}
						}
					}
				}
			} catch (NwtisRestIznimka e) {
				System.out.println("DOLASCI - Nema podataka za: " + a);
				BazaAerodromi.dodajUAerodromProblemiIznimkeDolasci(a, kon);
			}
			try {
				sleep(vrijemePauza);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long radDretveKraj = System.currentTimeMillis();
		long efektivnoVrijemeDretve = radDretveKraj - vrijemeAplikacije;
		long spavanjeDretve = izracunajSpavanjeDretve(efektivnoVrijemeDretve);
		System.out.println(stvarniBrojac);
		try {
			sleep(spavanjeDretve);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		dateTimeOdEpoch += 3600 * preuzimanjeVrijeme;
		}
	}
	/**
	 * Metoda u kojoj se osigurava da se ne preuzimaju najnoviji podaci spavanjem dretve jedan dan
	 * @param vrijemeOd
	 * @param vrijemeApp
	 * @param odmak
	 */
	private void spavanjeDretveDan(long vrijemeDo, long vrijemeApp, long odmak) {
		if((vrijemeApp*1L/1000) <= (vrijemeDo - (86400 * odmak))) {
			try {
				int uvecanjeVirtualnogBrojaca = 86400 / vrijemeCiklusa;
				virtualniBrojac += uvecanjeVirtualnogBrojaca;
				stvarniBrojac ++;
				sleep(86400 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Metoda u kojoj se za svakih 10 ciklusa korigira vrijeme
	 * @param sBrojac
	 */
	private void korekcija(long sBrojac) {
		if(sBrojac % korekcijaVremena == 0) {
			vrijemeAplikacije = prvoVrijemeAplikacije+(virtualniBrojac * vrijemeCiklusa * 1000);
		}
	}
	/**
	 * Metoda koja izračunava koliko dretva mora spavati u jednom ciklusu kako bi svi ciklusi bili jednaki
	 * @param efektivnoVrijemeDretve
	 * @return
	 */
	private long izracunajSpavanjeDretve(long efektivnoVrijemeDretve) {
		long ostatakPriDjeljenju;
        long rezultat;
        long odgovor = 0;
        if(efektivnoVrijemeDretve < (vrijemeCiklusa * 1000)){
            odgovor = (vrijemeCiklusa*1000) - efektivnoVrijemeDretve;
            virtualniBrojac++;
        }
        else {
        	ostatakPriDjeljenju = efektivnoVrijemeDretve % (vrijemeCiklusa * 1000);
        	rezultat = efektivnoVrijemeDretve / (vrijemeCiklusa * 1000);
        	if(ostatakPriDjeljenju == 0){
                odgovor = 0;
                virtualniBrojac += rezultat;
            }
        	else {
        		rezultat++;
        		virtualniBrojac += rezultat;
        		odgovor = (rezultat * vrijemeCiklusa * 1000) - efektivnoVrijemeDretve;
        	}
        }
        stvarniBrojac++;
        return odgovor;
	}
	
	/**
	 * Metoda za uplitanje u rad dretve
	 */
	@Override
	public void interrupt() {
		super.interrupt();
	}

}
