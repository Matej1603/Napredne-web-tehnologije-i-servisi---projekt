package org.foi.nwtis.mdurasek.aplikacija_3.podaci;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor()
public class Odgovor {
	
	@Getter
    @Setter
    String adresa;
	
	@Getter
    @Setter
    int port;
}
