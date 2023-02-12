package org.foi.nwtis.mdurasek.aplikacija_4.podaci;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

public class KorisnikGlavni {
	/**
	 * Metoda za slanje korisničkog unosa na server
	 * @param adresa
	 * @param port
	 * @param komanda
	 * @return
	 */
	public String posaljiKomandu(String adresa, int port, String komanda) {
        try (
                 Socket veza = new Socket(adresa, port);
                 InputStreamReader isr = new InputStreamReader(veza.getInputStream(),
                        Charset.forName("UTF-8"));
                 OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(),
                        Charset.forName("UTF-8"));) {  

            osw.write(komanda);
            osw.flush();
            veza.shutdownOutput();
            StringBuilder tekst = new StringBuilder();
            while (true) {
                int i = isr.read();
                if (i == -1) {
                    break;
                }
                tekst.append((char) i);
            }
            veza.shutdownInput();
            veza.close();
            return tekst.toString();
        } catch (SocketException e) {
            ispis(e.getMessage());
        } catch (IOException ex) {
            ispis(ex.getMessage());
        }
        return null;
    }

	/**
	 * Metoda za ispis poruke u slučaju iznimke
	 * @param message
	 */
	private void ispis(String message) {
		System.out.println(message);
	}
}
