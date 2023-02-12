package org.foi.nwtis.mdurasek.aplikacija_2.rest;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
/**
 * Klasa modela problema
 * @author NWTiS_3
 *
 */
@AllArgsConstructor()
public class Problemi {
	
	@Getter
    @Setter
    String ident;

    @Getter
    @Setter
    String description;
    
    @Getter
    @Setter
    Timestamp stored;

}
