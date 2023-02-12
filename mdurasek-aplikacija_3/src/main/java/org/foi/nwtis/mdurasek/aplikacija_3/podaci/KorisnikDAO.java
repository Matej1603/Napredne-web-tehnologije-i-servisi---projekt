package org.foi.nwtis.mdurasek.aplikacija_3.podaci;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;

public class KorisnikDAO {
	
	/**
	 * Dohvaćanje svih korisnika iz baze
	 * @param pbp
	 * @return
	 */
	public static List<Korisnik> dohvatiSveKorisnike(KonfiguracijaBP pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT * FROM korisnici";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            List<Korisnik> korisnici = new ArrayList<>();

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     Statement s = con.createStatement();
                     ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    String korisnik = rs.getString("korisnik");
                    String lozinka = rs.getString("lozinka");
                    String ime = rs.getString("ime");
                    String prezime = rs.getString("prezime");
                    String email = rs.getString("email");
                    Korisnik k = new Korisnik(korisnik,lozinka, prezime, ime, email);
                    korisnici.add(k);
                }
                return korisnici;

            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
	/**
	 * Dohvaćanje jednog korisnika
	 * @param pbp
	 * @param korisnickoIme
	 * @return
	 */
	public static List<Korisnik> dohvatiKorisnika(KonfiguracijaBP pbp,String korisnickoIme) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT * FROM korisnici WHERE korisnik = ?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            List<Korisnik> korisnici = new ArrayList<>();

            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {
				s.setString(1, korisnickoIme);
				ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    String korisnik = rs.getString("korisnik");
                    String lozinka = rs.getString("lozinka");
                    String ime = rs.getString("ime");
                    String prezime = rs.getString("prezime");
                    String email = rs.getString("email");
                    Korisnik k = new Korisnik(korisnik,lozinka, prezime, ime, email);
                    korisnici.add(k);
                }
                return korisnici;

            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
	
	/**
	 * Metoda za vraćanje svih grupa jednog korisnika
	 * @param pbp
	 * @param korisnickoIme
	 * @return
	 */
	public static List<Grupa> vratiGrupeKorisnika(KonfiguracijaBP pbp,String korisnickoIme) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT g.grupa,g.naziv FROM grupe g,uloge u WHERE u.grupa = g.grupa and u.korisnik = ?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            List<Grupa> grupe = new ArrayList<>();

            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {
				s.setString(1, korisnickoIme);
				ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    String grupa = rs.getString("grupa");
                    String naziv = rs.getString("naziv");
                    Grupa g = new Grupa(grupa,naziv);
                    grupe.add(g);
                }
                return grupe;

            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
	/**
	 * Dodavanje novog reda u tablicu korisnici
	 * @param k
	 * @param pbp
	 * @return
	 */
    public static boolean dodajKorisnika(Korisnik k, KonfiguracijaBP pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO korisnici (korisnik, ime, prezime, lozinka, email) "
                + "VALUES (?, ?, ?, ?, ?)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, k.korisnik);
                s.setString(2, k.ime);
                s.setString(3, k.prezime);
                s.setString(4, k.lozinka);
                s.setString(5, k.email);
                
                int brojAzuriranja = s.executeUpdate();

                return brojAzuriranja == 1;

            } catch (Exception ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
