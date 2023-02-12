package org.foi.nwtis.mdurasek.aplikacija_3.podaci;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.AvionLetiID;
import org.foi.nwtis.rest.podaci.Lokacija;

public class BazaKorisnik {

	/**
	 * Metoda za provjeru korisnika (kor.ime i lozinka)
	 * @param pbp
	 * @param korisnickoIme
	 * @param lozinka
	 * @return
	 */
	public static boolean provjeriKorisnika(KonfiguracijaBP pbp, String korisnickoIme, String lozinka) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT * FROM korisnici WHERE korisnik = ? AND lozinka = ?";
		boolean pronaden = false;
		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {
				s.setString(1, korisnickoIme);
				s.setString(2, lozinka);
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					pronaden = true;
				}
			} catch (SQLException ex) {
				Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
			}

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
		}
		return pronaden;
	}

	/**
	 * Dodavanje novog reda u tablicu tokeni
	 * @param pbp
	 * @param korisnickoIme
	 * @param trajanjeZetona
	 * @return
	 */
	public static ArrayList<Token> stvoriZeton(KonfiguracijaBP pbp, String korisnickoIme, int trajanjeZetona) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getAdminUsername();
		String bplozinka = pbp.getAdminPassword();
		String upit = "INSERT INTO tokeni (korisnik, aktivan) VALUES (?, ?);";
		ArrayList<Token> tokeni = new ArrayList<>();
		try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     PreparedStatement s = con.prepareStatement(upit,Statement.RETURN_GENERATED_KEYS)) {
            	s.setString(1,korisnickoIme);
            	LocalDateTime current = LocalDateTime.now();
                Timestamp timestamp = Timestamp.valueOf(current.plusHours(trajanjeZetona));
            	s.setTimestamp(2,timestamp);
                int brojAzuriranja = s.executeUpdate();
                ResultSet rs = s.getGeneratedKeys();
                while(rs.next()) {
                	if(brojAzuriranja == 1) {
                    	int id = rs.getInt(1);
                    	Token novi = new Token(id, timestamp);
                    	tokeni.add(novi);
                    	return tokeni;
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
        }
		return tokeni;
	}
	/**
	 * Update tablice tokena gdje je manji stored od vremena kad se okine metoda, okida se pri svakom servisu
	 * @param pbp
	 */
	public static void osvjeziTablicuTokena(KonfiguracijaBP pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getAdminUsername();
		String bplozinka = pbp.getAdminPassword();
		String upit = "UPDATE tokeni SET status = 0 WHERE status = 1 AND aktivan < CURRENT_TIMESTAMP;";
		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {
					s.executeUpdate();
			} catch (SQLException ex) {
				Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
			}

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	/**
	 * Brisanje važećeg tokena 
	 * @param pbp
	 * @param token
	 */
	public static void deaktivirajToken(KonfiguracijaBP pbp,int token) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getAdminUsername();
		String bplozinka = pbp.getAdminPassword();
		String upit = "UPDATE tokeni SET status = 0 WHERE id = ?;";
		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {
					s.setInt(1,token);
					s.executeUpdate();
			} catch (SQLException ex) {
				Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
			}

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	/**
	 * Brisanje svih važećih tokena korisnika
	 * @param pbp
	 * @param korisnik
	 */
	public static void deaktivirajSveTokeneKorisnika(KonfiguracijaBP pbp,String korisnik) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getAdminUsername();
		String bplozinka = pbp.getAdminPassword();
		String upit = "UPDATE tokeni SET status = 0 WHERE korisnik = ?;";
		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {
					s.setString(1,korisnik);
					s.executeUpdate();
			} catch (SQLException ex) {
				Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
			}

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	/**
	 * Metoda koja vraća stanje tokena korisniku
	 * @param pbp
	 * @param korisnickoIme
	 * @param token
	 * @return
	 */
	public static boolean vratiStanjeTokenaKorisniku(KonfiguracijaBP pbp,String korisnickoIme,int token){
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT * FROM tokeni WHERE korisnik = ? AND status = 1 AND id = ?;";
		boolean pronaden = false;
		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {
				s.setString(1, korisnickoIme);
				s.setInt(2, token);
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					pronaden = true;
				}
			} catch (SQLException ex) {
				Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
			}

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
		}
		return pronaden;
	}
	/**
	 * Metoda koja osigurava da korisnik ne koristi u zahtjevu tuđi žeton
	 * @param pbp
	 * @param korisnickoIme
	 * @param token
	 * @return
	 */
	public static boolean pristupTudemTokenu(KonfiguracijaBP pbp,String korisnickoIme,int token){
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT * FROM tokeni WHERE korisnik = ? AND id = ?;";
		boolean pronaden = true;
		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {
				s.setString(1, korisnickoIme);
				s.setInt(2, token);
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					pronaden = false;
				}
			} catch (SQLException ex) {
				Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
			}

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
		}
		return pronaden;
	}
	
	/**
	 * Metoda koja se koristi kad se brišu svi tokeni korisnika, budući da se ne šalje žeton u zahtjevu traži se barem jedan aktivan da se odradi radnja do kraja
	 * @param pbp
	 * @param korisnickoIme
	 * @return
	 */
	public static boolean provjeraBaremJednogAktivnogTokena(KonfiguracijaBP pbp,String korisnickoIme){
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT * FROM tokeni WHERE korisnik = ? AND status = 1;";
		boolean pronaden = false;
		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {
				s.setString(1, korisnickoIme);
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					pronaden = true;
				}
			} catch (SQLException ex) {
				Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
			}

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
		}
		return pronaden;
	}
	/**
	 * Provjera ovlasti kod korisnika prije brisanja tuđih tokena
	 * @param pbp
	 * @param korisnickoIme
	 * @param grupa
	 * @return
	 */
	public static boolean provjeraAutorizacije(KonfiguracijaBP pbp,String korisnickoIme,String grupa){
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT * FROM uloge WHERE korisnik = ? AND grupa = ?;";
		boolean pronaden = false;
		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {
				s.setString(1, korisnickoIme);
				s.setString(2, grupa);
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					pronaden = true;
				}
			} catch (SQLException ex) {
				Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
			}

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(BazaKorisnik.class.getName()).log(Level.SEVERE, null, ex);
		}
		return pronaden;
	}
}

