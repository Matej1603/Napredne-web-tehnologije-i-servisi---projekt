package org.foi.nwtis.mdurasek.aplikacija_2.slusaci;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.foi.nwtis.mdurasek.aplikacija_2.dretve.PreuzimanjeRasporedaAerodroma;
import org.foi.nwtis.mdurasek.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Klasa za slušača aplikacije
 * @author NWTiS_3
 *
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {
	private static KonfiguracijaBP konf;
	String putanja;
	/**
	 * Konstruktor
	 */
    public SlusacAplikacije() {
       
    }
    /**
     * Metoda za inicijalizaciju konteksta
     */
    @Override
	public void contextInitialized(ServletContextEvent sce) {
  	ServletContext context = sce.getServletContext();
 	String nazivDatoteke = context.getInitParameter("konfiguracija");
 	putanja = context.getRealPath("/WEB-INF") + File.separator;
 	nazivDatoteke = putanja + nazivDatoteke; 	
	KonfiguracijaBP konfig = new PostavkeBazaPodataka(nazivDatoteke);
	try {
		konfig.ucitajKonfiguraciju();
	} catch (NeispravnaKonfiguracija e) {		
		e.printStackTrace();
		return;
	}
  	context.setAttribute("Postavke", konfig);
  	ucitajKonfiguracijuKonteksta(context);
    PreuzimanjeRasporedaAerodroma pra = new PreuzimanjeRasporedaAerodroma();
    pra.start();
	ServletContextListener.super.contextInitialized(sce);
	}
    
    /**
     * Metoda za prosljeđivanje konfiguracije potrebnim metodama
     * @return
     */
    public static KonfiguracijaBP posaljiKonfiguracijuDretvi() {
    	return konf;
    }
    /**
     * Metoda za učitavanje konfiguracija iz contexta
     * @param context
     */
    public void ucitajKonfiguracijuKonteksta(ServletContext context) {
    	KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");
    	konf = konfig;
    }
	/**
	 * Metoda za brisanje kontexta
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
		ServletContext context = sce.getServletContext();
		KonfiguracijaBP k = (KonfiguracijaBP) context.getAttribute("Postavke");
		context.removeAttribute("Postavke");
		PreuzimanjeRasporedaAerodroma.currentThread().interrupt();
		System.out.println("Postavke obrisane!");
		ServletContextListener.super.contextDestroyed(sce);
	}
    
}
