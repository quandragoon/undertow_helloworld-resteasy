package helloworld.rest;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.apache.http.client.HttpClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by qdnguyen on 3/26/17.
 */
public class UndertowServerTest {
    private int port;
    private int httpPort;
    private HttpClient client;

    @Mocked
    private Config mockedConfig;

    @Before
    public void setup() throws Exception {
        start();
    }

    @After
    public void tearDown() throws Exception {
        UndertowServer.stop();
    }

    public static int[] getAvailPorts () throws IOException {
        try (ServerSocket ss1 = new ServerSocket(0); ServerSocket ss2 = new ServerSocket(0)) {
            return new int[]{ss1.getLocalPort(), ss2.getLocalPort()};
        }
    }

    private void start() throws Exception {
        UndertowServer server = new UndertowServer();
        int [] ports = getAvailPorts();
        port = ports[0];
        httpPort = ports[1];
        new Expectations() {{
            mockedConfig.getPort(); result = port;
            mockedConfig.getHttpPort(); result = httpPort;
            // mockedConfig.getKeyStoreFile(); result = "keystore";
        }};
        UndertowServer.startUndertow();
    }

    @Test
    public void testIsRunning () {
        Assert.assertTrue(UndertowServer.isRunning());
    }

    /*
    @Test (expected = FileNotFoundException.class)
    public void testOpenKeystoreNotFound () throws Exception {
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }
        };

        String[] args = {};
        UndertowServer.main(args);
    }
    */

    @Test
    public void testStartServer () throws Exception {
        new MockUp<UndertowServer>() {
            @Mock
            public void startUndertow () {
                return;
            }
        };
        String[] args = {};
        UndertowServer.main(args);
    }
}
