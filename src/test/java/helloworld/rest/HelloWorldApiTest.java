package helloworld.rest;

import mockit.Expectations;
import mockit.Mocked;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by qdnguyen on 3/26/17.
 */
public class HelloWorldApiTest {

    static class TrustAllSslClientFactory {
        public static HttpClient createClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(),
                    new NoopHostnameVerifier());
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        }
    }

    private int port;
    private int httpPort;
    private HttpClient client;

    @Mocked
    private Config mockedConfig;

    private void startServer() {
        for (int i = 0; i < 10; ++i) {
            try {
                int[] ports = UndertowServerTest.getAvailPorts();
                port = ports[0];
                httpPort = ports[1];
                new Expectations() {{
                    mockedConfig.getPort(); result = port;
                    mockedConfig.getHttpPort(); result = httpPort;
                }};
                UndertowServer.startUndertow();
                client = TrustAllSslClientFactory.createClient();
                return;
            } catch (Exception ignored) {}
        }
        throw new IllegalStateException("No free port after 10 attempts");
    }

    @Test
    public void testHelloWorld() throws Exception {
        startServer();
        HttpGet get = new HttpGet("http://localhost:" + port + "/helloworld");
        HttpResponse response = client.execute(get);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        Assert.assertEquals("Hello World", EntityUtils.toString(response.getEntity()));
    }
}
