package org.foi.nwtis.mdurasek.aplikacija_3.podaci;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.foi.nwtis.mdurasek.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLetiID;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 * Klasa za komunikaciju s bazom podataka
 * @author NWTiS_3
 *
 */
public class AerodromDAO {
	
	
	/**
	 * Metoda za provjere podataka u pracenim aerodromima kako se nebi inserto dvaput aerodrom s istim icaom
	 * @param pbp
	 * @param icao
	 * @return
	 */
	public static boolean provjeriIcaoUPracenima(KonfiguracijaBP pbp,String icao) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT ident FROM AERODROMI_PRACENI;";
		boolean postoji = false;
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
			for (String aerodrom : aerodromiPraceni) {
				if(aerodrom.equals(icao)) {
					postoji = true;
					break;
				}
			}				
			} catch (SQLException ex) {
				Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return postoji;
	}
	
	/**
	 * Metoda za dodavanje novog reda u tablicu AERODROM_PRACENI
	 * @param pbp
	 * @param icao
	 * @return
	 */
	public static boolean dodajAerodromZaPracenje(KonfiguracijaBP pbp, String icao) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getAdminUsername();
		String bplozinka = pbp.getAdminPassword();
		String upit = "INSERT INTO AERODROMI_PRACENI (ident, `stored`) VALUES (?, ?);";
		try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     PreparedStatement s = con.prepareStatement(upit)) {
            	s.setString(1,icao);
            	Date date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());
            	s.setTimestamp(2,timestamp);
                int brojAzuriranja = s.executeUpdate();
                return brojAzuriranja == 1;

            } catch (Exception ex) {
                Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
		return false;
	}
	/**
	 * Metoda za dohvaćanje svih podataka iz tablice airports
	 * @param pbp
	 * @return
	 */
	public static List<Aerodrom> dohvatiSveAerodrome(KonfiguracijaBP pbp){
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT * FROM airports;";
		try {
			Class.forName(pbp.getDriverDatabase(url));
			List<Aerodrom> aerodromi = new ArrayList<Aerodrom>();
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					Statement s = con.createStatement();
					ResultSet rs = s.executeQuery(upit)) {
				while (rs.next()) {
					Aerodrom novi = new Aerodrom();
					String icao = rs.getString("ident");
					String naziv = rs.getString("name");
					String drzava  = rs.getString("iso_country");
					String lokacija = rs.getString("coordinates");
					String koordinate[] = lokacija.split(",");
					Lokacija novaLokacija = new Lokacija();
					novaLokacija.setLatitude(koordinate[0]);
					novaLokacija.setLongitude(koordinate[1]);
				    novi.setIcao(icao);
				    novi.setNaziv(naziv);
				    novi.setDrzava(drzava);
				    novi.setLokacija(novaLokacija);
					aerodromi.add(novi);
				}
				return aerodromi;
				
			} catch (SQLException ex) {
				Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	/**
	 * Metoda za dohvaćanje svih podataka iz tablice airports
	 * @param pbp
	 * @return
	 */
	public static List<Aerodrom> dohvatiSvePraceneAerodrome(KonfiguracijaBP pbp){
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT * FROM airports a,AERODROMI_PRACENI ap WHERE ap.ident = a.ident;";
		try {
			Class.forName(pbp.getDriverDatabase(url));
			List<Aerodrom> aerodromi = new ArrayList<Aerodrom>();
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					Statement s = con.createStatement();
					ResultSet rs = s.executeQuery(upit)) {
				while (rs.next()) {
					Aerodrom novi = new Aerodrom();
					String icao = rs.getString("ident");
					String naziv = rs.getString("name");
					String drzava  = rs.getString("iso_country");
					String lokacija = rs.getString("coordinates");
					String koordinate[] = lokacija.split(",");
					Lokacija novaLokacija = new Lokacija();
					novaLokacija.setLatitude(koordinate[0]);
					novaLokacija.setLongitude(koordinate[1]);
				    novi.setIcao(icao);
				    novi.setNaziv(naziv);
				    novi.setDrzava(drzava);
				    novi.setLokacija(novaLokacija);
					aerodromi.add(novi);
				}
				return aerodromi;
				
			} catch (SQLException ex) {
				Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	
	/**
	 * Metoda koja dohvaća dolaske na aerodrom u određenom intervalu
	 * @param pbp
	 * @param icao
	 * @param odDatuma
	 * @param doDatuma
	 * @param vrsta
	 * @return
	 */
	public static ArrayList<AvionLetiID> dohvatiDolaskeZaIcaoNaDatum(KonfiguracijaBP pbp,String icao,String odDatuma,String doDatuma,String vrsta){
		ArrayList<AvionLetiID> avioni = new ArrayList<AvionLetiID>();
		long vrijemeOd = 0;
		long vrijemeDo = 0;
		boolean istiDatum = false;
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT * FROM AERODROMI_DOLASCI WHERE firstSeen > ? AND firstSeen < ? AND estArrivalAirport = ?;";
		if(vrsta.equals("0")) {
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			try {
				if(odDatuma.equals(doDatuma)) {
					istiDatum = true;
				}
				Date dateOd = df.parse(odDatuma);
				Date dateDo = df.parse(doDatuma);
				vrijemeOd = dateOd.getTime();
				vrijemeDo = dateDo.getTime();
				vrijemeOd = vrijemeOd/1000;
				vrijemeDo = vrijemeDo/1000;
				if(istiDatum == true) {
					vrijemeDo+=86399;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(vrsta.equals("1")) {
			vrijemeOd = Integer.parseInt(odDatuma);
			vrijemeDo = Integer.parseInt(doDatuma);
		}
		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {
				s.setInt(1,(int)vrijemeOd);
				s.setInt(2, (int)vrijemeDo);
				s.setString(3, icao);
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					int id = rs.getInt("id");
					String icao24 = rs.getString("icao24");
					int firstSeen = rs.getInt("firstSeen");
					String estDeparture  = rs.getString("estDepartureAirport");
					int lastSeen = rs.getInt("lastSeen");
					String estArrival = rs.getString("estArrivalAirport");
					String callSign = rs.getString("callsign");
					int estDepartureHoriz = rs.getInt("estDepartureAirportHorizDistance");
					int estDepartureVert = rs.getInt("estDepartureAirportVertDistance");
					int estArrivalHoriz = rs.getInt("estArrivalAirportHorizDistance");
					int estArrivalVert = rs.getInt("estArrivalAirportVertDistance");
					int departureCandidate = rs.getInt("departureAirportCandidatesCount");
					Integer arrivalCandidate = rs.getInt("arrivalAirportCandidatesCount");
					AvionLetiID novi = new AvionLetiID(id,icao24,firstSeen,estDeparture,lastSeen,estArrival,
							callSign,estDepartureHoriz,estDepartureVert,estArrivalHoriz,estArrivalVert,
							departureCandidate,arrivalCandidate);
					avioni.add(novi);
				}
				
			} catch (SQLException ex) {
				Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return avioni;
	}
	
	/**
	 * Metoda koja dohvaća polaske na aerodrom u određenom intervalu
	 * @param pbp
	 * @param icao
	 * @param odDatuma
	 * @param doDatuma
	 * @param vrsta
	 * @return
	 */
	public static ArrayList<AvionLetiID> dohvatiPolaskeZaIcaoNaDatum(KonfiguracijaBP pbp,String icao,String odDatuma,String doDatuma,String vrsta){
		ArrayList<AvionLetiID> avioni = new ArrayList<AvionLetiID>();
		long vrijemeOd = 0;
		long vrijemeDo = 0;
		boolean istiDatum = false;
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT * FROM AERODROMI_POLASCI WHERE firstSeen > ? AND firstSeen < ? AND estDepartureAirport = ?;";
		if(vrsta.equals("0")) {
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			try {
				if(odDatuma.equals(doDatuma)) {
					istiDatum = true;
				}
				Date dateOd = df.parse(odDatuma);
				Date dateDo = df.parse(doDatuma);
				vrijemeOd = dateOd.getTime();
				vrijemeDo = dateDo.getTime();
				vrijemeOd = vrijemeOd/1000;
				vrijemeDo = vrijemeDo/1000;
				if(istiDatum == true) {
					vrijemeDo+=86399;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(vrsta.equals("1")) {
			vrijemeOd = Integer.parseInt(odDatuma);
			vrijemeDo = Integer.parseInt(doDatuma);
		}
		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {
				s.setInt(1,(int)vrijemeOd);
				s.setInt(2, (int)vrijemeDo);
				s.setString(3, icao);
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					int id = rs.getInt("id");
					String icao24 = rs.getString("icao24");
					int firstSeen = rs.getInt("firstSeen");
					String estDeparture  = rs.getString("estDepartureAirport");
					int lastSeen = rs.getInt("lastSeen");
					String estArrival = rs.getString("estArrivalAirport");
					String callSign = rs.getString("callsign");
					int estDepartureHoriz = rs.getInt("estDepartureAirportHorizDistance");
					int estDepartureVert = rs.getInt("estDepartureAirportVertDistance");
					int estArrivalHoriz = rs.getInt("estArrivalAirportHorizDistance");
					int estArrivalVert = rs.getInt("estArrivalAirportVertDistance");
					int departureCandidate = rs.getInt("departureAirportCandidatesCount");
					Integer arrivalCandidate = rs.getInt("arrivalAirportCandidatesCount");
					AvionLetiID novi = new AvionLetiID(id,icao24,firstSeen,estDeparture,lastSeen,estArrival,
							callSign,estDepartureHoriz,estDepartureVert,estArrivalHoriz,estArrivalVert,
							departureCandidate,arrivalCandidate);
					avioni.add(novi);
				}
				
			} catch (SQLException ex) {
				Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return avioni;
	}
}
