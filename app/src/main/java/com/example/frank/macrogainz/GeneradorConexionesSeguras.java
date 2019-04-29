package com.example.frank.macrogainz;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class GeneradorConexionesSeguras {

    private static GeneradorConexionesSeguras instancia;

    private GeneradorConexionesSeguras(){
    }

    public static GeneradorConexionesSeguras getInstance(){
        if (instancia==null){
            instancia = new GeneradorConexionesSeguras();
        }
        return instancia;
    }

    public HttpsURLConnection crearConexionSegura (Context contexto, String direccion) {
        HttpsURLConnection urlConnection=null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream caInput = new BufferedInputStream(contexto.getAssets().open("certificado.crt"));
            Certificate ca = cf.generateCertificate(caInput);

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            URL destino = new URL(direccion);
            urlConnection = (HttpsURLConnection) destino.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return urlConnection;
    }

}
