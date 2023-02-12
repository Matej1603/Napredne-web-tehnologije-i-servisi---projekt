SELECT * FROM AERODROMI_PRACENI ap ; #Dohvati praćene aerodrome
SELECT COUNT(*) FROM AERODROMI_PRACENI ap ; #Broj praćenih
SELECT COUNT(*) FROM AERODROMI_DOLASCI ad ; #Broj dolazaka
SELECT COUNT(*) FROM AERODROMI_POLASCI ap ; #Broj polazaka

SELECT DISTINCT FROM_UNIXTIME(firstSeen,"%m %d") FROM AERODROMI_DOLASCI ad ORDER BY 1; #Dolasci preuzimani dani
SELECT DISTINCT FROM_UNIXTIME(firstSeen,"%m %d") FROM AERODROMI_POLASCI ap ORDER BY 1; #Polasci preuzimani dani

SELECT COUNT(*), FROM_UNIXTIME(firstSeen,"%m %d") FROM AERODROMI_DOLASCI ad GROUP BY 2 ORDER BY 2 ASC; #Broj podataka po danu dolasci
SELECT COUNT(*), FROM_UNIXTIME(firstSeen,"%m %d") FROM AERODROMI_POLASCI ap GROUP BY 2 ORDER BY 2 ASC; #Broj podataka po danu polasci

SELECT COUNT(*), FROM_UNIXTIME(firstSeen,"%m %d"), ad.estArrivalAirport FROM AERODROMI_DOLASCI ad GROUP BY 2,3 ORDER BY 2; #Dolasci po danu za svaki aerodrom
SELECT COUNT(*), FROM_UNIXTIME(firstSeen,"%m %d"), ap.estDepartureAirport FROM AERODROMI_POLASCI ap GROUP BY 2,3 ORDER BY 2; #Polasci po danu za svaki aerodrom

SELECT COUNT(*), FROM_UNIXTIME(firstSeen,"%m %d"),ad.estArrivalAirport FROM AERODROMI_DOLASCI ad WHERE ad.estArrivalAirport = "OMDB" GROUP BY 2 ORDER BY 2 ASC; #Dolasci po danu jedan aerodrom
SELECT COUNT(*), FROM_UNIXTIME(firstSeen,"%m %d"),ap.estDepartureAirport FROM AERODROMI_POLASCI ap WHERE ap.estDepartureAirport = "OMDB" GROUP BY 2 ORDER BY 2 ASC; #Polasci po danu jedan aerodrom