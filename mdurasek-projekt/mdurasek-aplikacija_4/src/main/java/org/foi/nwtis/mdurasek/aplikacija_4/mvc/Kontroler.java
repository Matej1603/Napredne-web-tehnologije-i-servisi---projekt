package org.foi.nwtis.mdurasek.aplikacija_4.mvc;

import java.util.List;

import org.foi.nwtis.mdurasek.aplikacija_4.podaci.Korisnik;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;


/**
 * Klasa kontrolera web aplikacije
 * @author NWTiS_3
 *
 */
@Controller
@Path("projekt")
@RequestScoped

public class Kontroler {
	@Inject 
	private Models model;
	public static String p = "";
	public static String r = "";
	@GET
	@Path("pocetak")
	@View("index.jsp")
	public void pocetak() {
	}

	@GET
	@Path("pogled_4.1")
	@View("pogled_4.1.jsp")
	public void registracijaKorisnika() {
		r = "";
	}
	@GET
	@Path("pogled_4.1/{korisnik}/{lozinka}/{prezime}/{ime}/{email}")
	@View("pogled_4.1.jsp")
	public void registracijaKorisnika(@PathParam("korisnik")String korisnik,@PathParam("lozinka")String lozinka,@PathParam("prezime")String prezime,@PathParam("ime")String ime,@PathParam("email")String email) {
		ProjektKlijent ak = new ProjektKlijent();
		r = ak.registracijaKorisnika(korisnik, lozinka, prezime, ime, email);
		model.put("reg", r);
	}
	@GET
	@Path("pogled_4.2")
	@View("pogled_4.2.jsp")
	public void prijavaKorisnika(){
		p = "";
	}
	@GET
	@Path("pogled_4.2/{korisnik}/{lozinka}")
	@View("pogled_4.2.jsp")
	public void prijavaKorisnika(@PathParam("korisnik")String korisnik,@PathParam("lozinka")String lozinka) {
		ProjektKlijent ak = new ProjektKlijent();
		p =  ak.prijavaKorisnika(korisnik, lozinka);
		model.put("prijava", p);
	}
	
	@GET
	@Path("pogled_4.3")
	@View("pogled_4.3.jsp")
	public void pregledKorisnika() {
		ProjektKlijent ak = new ProjektKlijent();
		List<Korisnik> korisnici = ak.dajSveKorisnike();
		model.put("korisnici",korisnici);
	}
	
	@GET
	@Path("pogled_4.3/d")
	@View("index.jsp")
	public void obrisiToken() {
		ProjektKlijent ak = new ProjektKlijent();
		ak.obrisiToken();
	}
	
	@GET
	@Path("pogled_4.4")
	@View("pogled_4.4.jsp")
	public void naredbe() {
		ProjektKlijent ak = new ProjektKlijent();
		String status = ak.vratiStatus();
		model.put("status",status);
		model.put("greska","");
		
	}
	@GET
	@Path("pogled_4.4/{naredba}")
	@View("pogled_4.4.jsp")
	public void naredbe(@PathParam("naredba")String naredba) {
		model.put("greska","");
		ProjektKlijent ak = new ProjektKlijent();
		String server = ak.obradiNaredbu(naredba);
		if(server.contains("ERROR")) {
			model.put("greska",server);
		}
		String status = ak.vratiStatus();
		model.put("status",status);
	}

}
