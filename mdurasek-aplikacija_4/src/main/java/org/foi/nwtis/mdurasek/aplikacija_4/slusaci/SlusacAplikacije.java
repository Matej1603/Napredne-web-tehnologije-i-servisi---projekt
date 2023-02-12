package org.foi.nwtis.mdurasek.aplikacija_4.slusaci;

import java.io.File;

import org.foi.nwtis.mdurasek.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Klasa slušača aplikacije
 * @author NWTiS_3
 *
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

	private static KonfiguracijaBP konf;
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
		String putanja = context.getRealPath("/WEB-INF") + File.separator;
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
		ServletContextListener.super.contextInitialized(sce);
	}
    
    /**
     * Metoda za učitavanje konfiguracije iz konteksta
     * @param context
     */
    public void ucitajKonfiguracijuKonteksta(ServletContext context) {
    	KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");
    	konf = konfig;
    }
    
    /**
     * Metoda za prosljeđivanje konfiguracije
     * @return
     */
    public static KonfiguracijaBP posaljiKonfiguraciju() {
    	return konf;
    }
    /**
     * Metoda za brisanje konteksta
     */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		context.removeAttribute("Postavke");
		System.out.println("Postavke obrisane!");
		
		ServletContextListener.super.contextDestroyed(sce);
	}
    
}
