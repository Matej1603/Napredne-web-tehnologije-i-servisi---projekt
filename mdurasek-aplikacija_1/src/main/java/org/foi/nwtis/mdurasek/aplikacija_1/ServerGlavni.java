package org.foi.nwtis.mdurasek.aplikacija_1;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.foi.nwtis.mdurasek.vjezba_03.konfiguracije.Konfiguracija;
import org.foi.nwtis.mdurasek.vjezba_03.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mdurasek.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.podaci.Aerodrom;


/**
 * Klasa za glavni server
 * @author NWTiS_3
 *
 */
public class ServerGlavni {

	private int maxCekaca = -1;
	public static int port = 0;
	public static String adresa = "";
	private Socket veza = null;
	private static Konfiguracija konf = null; 
	public static int stanjeServera = 0;
	public static ArrayList<Aerodrom> aerodromi = new ArrayList<Aerodrom>();
	public static ArrayList<Aerodrom> aerodromPrvi = new ArrayList<Aerodrom>();
	public static ArrayList<Aerodrom> aerodromDrugi = new ArrayList<Aerodrom>();

	/**
	 * Glavna metoda ServerGlavni
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Nije definiran parametar konfiguracijske datoteke!");
			return;
		}
		if(!ucitajKonfiguracijskePodatke(args)) return;
		int port = Integer.parseInt(konf.dajPostavku("port"));
		int maxCekaca =  Integer.parseInt(konf.dajPostavku("maks.cekaca"));
		adresa = (konf.dajPostavku("server.glavni.adresa"));
		ServerGlavni server  = new ServerGlavni(port,maxCekaca);
		server.obradaZahtjeva();
	}

	/**
	 * Metoda za čitanje konfiguracije iz datoteke
	 * @param args
	 * @return
	 */
	public static boolean ucitajKonfiguracijskePodatke(String[] args) {
		String nazivDatoteke = args[0];
		try {
			konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
		} catch (NeispravnaKonfiguracija e) {
			System.out.println("Problem s učitavnjem datoteke "+nazivDatoteke+"!");
			return false;
		}
		return true;
	}

	/**
	 * Konstruktor za ServerGlavni
	 * @param port
	 * @param maxCekaca
	 */
	public ServerGlavni(int port, int maxCekaca) {
		this.port = port;
		this.maxCekaca = maxCekaca;
	}
	
	/**
	 * Metoda u kojoj se poziva dretva za daljnje izvršavanje korisničkog zahtjeva
	 */
	public void obradaZahtjeva() {
		try (ServerSocket ss = new ServerSocket(this.port, this.maxCekaca)) {			
			while (true) {
				this.veza = ss.accept();
				DretvaZahtjeva novaDretva = new DretvaZahtjeva(veza,konf);
		        novaDretva.start();
				} 
			}
		catch (IOException ex) {
			System.out.println("ERROR 49 PORT JE ZAUZET");
		}
	}
	
	/**
	 * Metoda kojom se gasi aplikacija_1
	 */
	public static void srusiServer() {
		DretvaZahtjeva.currentThread().interrupt();
		System.exit(0);
	}
}
