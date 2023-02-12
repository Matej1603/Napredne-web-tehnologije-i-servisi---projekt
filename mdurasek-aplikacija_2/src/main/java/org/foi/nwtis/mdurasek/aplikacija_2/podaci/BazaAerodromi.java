package org.foi.nwtis.mdurasek.aplikacija_2.podaci;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mdurasek.aplikacija_2.rest.Problemi;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.AvionLetiID;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 * Klasa za komunikaciju s bazom podataka
 * @author NWTiS_3
 *
 */
public class BazaAerodromi {
	/**
	 * Metoda koja vraca icao za svaki red u tablici AERODROMI_PRACENI
	 * @param pbp
	 * @return
	 */
	public static List<String> dohvatiSvePraceneAerodrome(KonfiguracijaBP pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT ident FROM AERODROMI_PRACENI;";
		try {
			Class.forName(pbp.getDriverDatabase(url));
			List<String> aerodromiPraceni = new ArrayList<String>();
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					Statement s = con.createStatement();
					ResultSet rs = s.executeQuery(upit)) {
				while (rs.next()) {
					String ident = rs.getString("ident");
					aerodromiPraceni.add(ident);
				}
				return aerodromiPraceni;
				
			} catch (SQLException ex) {
				Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	/**
	 * Dodavanje reda u tablicu AERODROMI_PROBLEMI ukoliko je polazni aerodrom bio null
	 * @param avion
	 * @param pbp
	 */
	public static void dodajUAerodromProblemiZaPolaske(AvionLeti avion, KonfiguracijaBP pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO AERODROMI_PROBLEMI (ident, description, `stored`) "
                + "VALUES (?, ?, ?)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, avion.getEstDepartureAirport());
                s.setString(2, "Dolazni aerodrom je null");
                Date date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());
                s.setTimestamp(3, timestamp);
                s.executeUpdate();
       
                
            } catch (Exception ex) {
                Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	/**
	 * Dodavanje reda u tablicu AERODROMI_PROBLEMI ukoliko se desila iznimka za polaske
	 * @param avion
	 * @param pbp
	 */
	public static void dodajUAerodromProblemiIznimkePolasci(String avion, KonfiguracijaBP pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO AERODROMI_PROBLEMI (ident, description, `stored`) "
                + "VALUES (?, ?, ?)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, avion);
                s.setString(2, "Nema podataka za "+avion+ " POLASCI");
                Date date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());
                s.setTimestamp(3, timestamp);
                s.executeUpdate();
            } catch (Exception ex) {
                Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	/**
	 * Dodavanje reda u tablicu AERODROMI_PROBLEMI ukoliko je dolazni aerodrom bio null
	 * @param avion
	 * @param pbp
	 */
	public static void dodajUAerodromProblemiZaDolaske(AvionLeti avion, KonfiguracijaBP pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO AERODROMI_PROBLEMI (ident, description, `stored`) "
                + "VALUES (?, ?, ?)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, avion.getEstArrivalAirport());
                s.setString(2, "Odlazni aerodrom je null");
                Date date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());
                s.setTimestamp(3, timestamp);
                s.executeUpdate();
                
            } catch (Exception ex) {
                Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	/**
	 * Dodavanje reda u tablicu AERODROMI_PROBLEMI ukoliko se desila iznimka za dolaske
	 * @param avion
	 * @param pbp
	 */
	public static void dodajUAerodromProblemiIznimkeDolasci(String avion, KonfiguracijaBP pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO AERODROMI_PROBLEMI (ident, description, `stored`) "
                + "VALUES (?, ?, ?)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, avion);
                s.setString(2, "Nema podataka za "+avion+ " DOLASCI");
                Date date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());
                s.setTimestamp(3, timestamp);
                s.executeUpdate();
            } catch (Exception ex) {
                Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	/**
	 * Dodavanje reda u tablicu AERODROMI_POLASCI
	 * @param avion
	 * @param pbp
	 */
	public static void dodajUAerodromPolasci(AvionLeti avion, KonfiguracijaBP pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO AERODROMI_POLASCI (icao24, firstSeen, estDepartureAirport,"
        		+ " lastSeen, estArrivalAirport, callsign, estDepartureAirportHorizDistance,"
        		+ " estDepartureAirportVertDistance, estArrivalAirportHorizDistance,"
        		+ " estArrivalAirportVertDistance, departureAirportCandidatesCount, arrivalAirportCandidatesCount, `stored`) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, avion.getIcao24());
                s.setInt(2, avion.getFirstSeen());
                s.setString(3, avion.getEstDepartureAirport());
                s.setInt(4, avion.getLastSeen());
                s.setString(5, avion.getEstArrivalAirport());
                s.setString(6, avion.getCallsign());
                s.setInt(7, avion.getEstDepartureAirportHorizDistance());
                s.setInt(8, avion.getEstDepartureAirportVertDistance());
                s.setInt(9, avion.getEstArrivalAirportHorizDistance());
                s.setInt(10, avion.getEstArrivalAirportVertDistance());
                s.setInt(11, avion.getDepartureAirportCandidatesCount());
                s.setInt(12, avion.getArrivalAirportCandidatesCount());
                Date date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());
                s.setTimestamp(13, timestamp);
                s.executeUpdate();

            } catch (Exception ex) {
                Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	/**
	 * Provjeravanje ključeva prije unosa u tablicu AERODROMI_DOLASCI kako se nebi dogadale iznimke
	 * @param pbp
	 * @param firstSeenKljuc
	 * @param icao24Kljuc
	 * @return
	 */
	public static boolean provjeriKljuceveZaDolaske(KonfiguracijaBP pbp,int firstSeenKljuc,String icao24Kljuc) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT * FROM AERODROMI_DOLASCI WHERE icao24 = ? AND firstSeen = ?";
        boolean postoji = false;
    	try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {
				s.setString(1, icao24Kljuc);
				s.setInt(2, firstSeenKljuc);
				ResultSet rs = s.executeQuery(); 
				while (rs.next()) {
					postoji = true;
				}
			} catch (SQLException ex) {
				Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
		}
		return postoji;
	}
	
	/**
	 * Provjeravanje ključeva prije unosa u tablicu AERODROMI_POLASCI kako se nebi dogadale iznimke
	 * @param pbp
	 * @param firstSeenKljuc
	 * @param icao24Kljuc
	 * @return
	 */
	public static boolean provjeriKljuceveZaPolaske(KonfiguracijaBP pbp,int firstSeenKljuc,String icao24Kljuc) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT * FROM AERODROMI_POLASCI WHERE icao24 = ? AND firstSeen = ?";
        boolean postoji = false;
    	try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {
				s.setString(1, icao24Kljuc);
				s.setInt(2, firstSeenKljuc);
				ResultSet rs = s.executeQuery(); 
				while (rs.next()) {
					postoji = true;
				}
			} catch (SQLException ex) {
				Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
		}
		return postoji;
	}
	/**
	 * Dodavanje novog reda u tablicu AERODROMI_DOLASCI
	 * @param avion
	 * @param pbp
	 */
	public static void dodajUAerodromDolasci(AvionLeti avion, KonfiguracijaBP pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO AERODROMI_DOLASCI (icao24, firstSeen, estDepartureAirport,"
        		+ " lastSeen, estArrivalAirport, callsign, estDepartureAirportHorizDistance,"
        		+ " estDepartureAirportVertDistance, estArrivalAirportHorizDistance,"
        		+ " estArrivalAirportVertDistance, departureAirportCandidatesCount, arrivalAirportCandidatesCount, `stored`) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, avion.getIcao24());
                s.setInt(2, avion.getFirstSeen());
                s.setString(3, avion.getEstDepartureAirport());
                s.setInt(4, avion.getLastSeen());
                s.setString(5, avion.getEstArrivalAirport());
                s.setString(6, avion.getCallsign());
                s.setInt(7, avion.getEstDepartureAirportHorizDistance());
                s.setInt(8, avion.getEstDepartureAirportVertDistance());
                s.setInt(9, avion.getEstArrivalAirportHorizDistance());
                s.setInt(10, avion.getEstArrivalAirportVertDistance());
                s.setInt(11, avion.getDepartureAirportCandidatesCount());
                s.setInt(12, avion.getArrivalAirportCandidatesCount());
                Date date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());
                s.setTimestamp(13, timestamp);
                s.executeUpdate();

            } catch (Exception ex) {
                Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BazaAerodromi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
