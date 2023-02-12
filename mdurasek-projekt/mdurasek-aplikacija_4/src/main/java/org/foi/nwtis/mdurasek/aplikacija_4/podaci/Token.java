package org.foi.nwtis.mdurasek.aplikacija_4.podaci;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor()
public class Token {
	
	@Getter
    @Setter
    int zeton;
	
	@Getter
    @Setter
    Timestamp vrijeme;
}
