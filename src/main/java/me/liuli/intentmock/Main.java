package me.liuli.intentmock;

import com.alibaba.dcm.DnsCacheManipulator;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import me.liuli.intentmock.context.HwidContext;
import me.liuli.intentmock.context.UserInfoContext;
import me.liuli.intentmock.context.VersionContext;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Main {

    private static boolean running = false;

    public static void main(String[] args) throws InterruptedException {
//        launch();
//        if(args.length > 0) {
//            try {
//                final Class<?> klass = Class.forName("net.minecraft.client.main.Main");
//                // get psvm method and invoke it
//                klass.getMethod("main", String[].class).invoke(null, (Object) args);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        Thread.sleep(1000);
        testRequest();
    }

    public static void premain(String agentArgs) {
        launch();
    }

    public static void launch() {
        if(running) {
            return;
        }

        try {
            startServer();
            disableSSL();
        } catch (Exception e) {
            e.printStackTrace();
        }

        log("IntentMock is running!");

        running = true;
    }

    public static void threadDumper(final long delay) {
        final Thread mainThread = Thread.currentThread();
        new Thread(() -> {
            while (true) {
                try {
                    // dump main thread stack
                    StringBuilder sb = new StringBuilder();
                    for (StackTraceElement element : mainThread.getStackTrace()) {
                        sb.append(element.toString()).append("\n");
                    }
                    log("Main thread stack:\n" + sb.toString());
                    Thread.sleep(delay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void startServer() throws Exception {
        final Class<? extends HttpContext>[] classes = new Class[] {
                UserInfoContext.class,
                VersionContext.class,
                HwidContext.class
        };

        final HttpsServer server = HttpsServer.create(new InetSocketAddress(443), 0);

        final char[] pwd = "password".toCharArray();

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(Objects.requireNonNull(Main.class.getResourceAsStream("/keystore.jks")), pwd);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, pwd);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom(new Date().toString().getBytes()));

        server.setHttpsConfigurator(new HttpsConfigurator(sslContext)
        {
            @Override
            public void configure(HttpsParameters params)
            {
                // L.trace("Generando contexto de seguridad HTTP para la conexion entrante desde la IP [{}]", params.getClientAddress().getAddress().toString().split("\\/")[1]);
                SSLContext c = this.getSSLContext();// SSLContext.getDefault();
                SSLEngine engine = c.createSSLEngine();
                params.setNeedClientAuth(false);
                params.setCipherSuites(engine.getEnabledCipherSuites());
                params.setProtocols(engine.getEnabledProtocols());
                SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
                params.setSSLParameters(defaultSSLParameters);
            }
        });

        final ArrayList<String> redirected = new ArrayList<>();
        for(Class<? extends HttpContext> klass : classes) {
            HttpContext context = klass.newInstance();
            server.createContext(context.getPath(), context);
            if(!redirected.contains(context.getURL())) {
                redirected.add(context.getURL());
            }
            log("Mocking " + context.getURL() + context.getPath() + "!");
        }

        server.setExecutor(null);
        server.start();

        for(String url : redirected) {
            DnsCacheManipulator.setDnsCache(url, "127.0.0.1");
        }
    }

    public static void disableSSL() throws GeneralSecurityException {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier validHosts = (arg0, arg1) -> true;
        // All hosts will be valid
        HttpsURLConnection.setDefaultHostnameVerifier(validHosts);
    }

    public static void log(String message) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "][IntentMock] " + message);
    }

    private static void testRequest() {
        try {
            System.out.println("response is " + getResponse("login?key=114514"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getResponse(final String getParameters) throws IOException {
        System.out.println("getParameters is " + getParameters);
        final HttpsURLConnection connection = (HttpsURLConnection) new URL("https://intent.store:4431/api/" + getParameters).openConnection();
        connection.addRequestProperty("User-Agent", "Intent-API/1.0 Rise");

        final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String lineBuffer;
        final StringBuilder response = new StringBuilder();
        while ((lineBuffer = reader.readLine()) != null)
            response.append(lineBuffer);

        return response.toString();
    }
}
