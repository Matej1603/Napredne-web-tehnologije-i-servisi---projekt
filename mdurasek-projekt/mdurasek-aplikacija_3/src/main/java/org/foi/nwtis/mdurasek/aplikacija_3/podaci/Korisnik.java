package org.foi.nwtis.mdurasek.aplikacija_3.podaci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor()
public class Korisnik {

	public Korisnik(){
		
	}
    @Getter
    @Setter
    String korisnik;

    @Getter
    @Setter
    String lozinka;
    
    @Getter
    @Setter
    String prezime;

    @Getter
    @Setter
    String ime;

    @Getter
    @Setter
    String email;
}