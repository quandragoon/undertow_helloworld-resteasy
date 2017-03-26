package helloworld.rest;

import com.google.common.io.Resources;
import mockit.Deencapsulation;
import mockit.Mock;
import mockit.MockUp;
import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConfigTest {
    private Config config;

    @Test
    public void testGetters() throws IOException {
        new MockUp<InetAddress> () {
            @Mock
            String getHostName() throws UnknownHostException {
                return "myHostName";
            }
        };
        config = (Config) new Yaml(new Constructor(Config.class)).load(
                Resources.toString(Resources.getResource("config.yml"), StandardCharsets.UTF_8)
        );
        Assert.assertEquals(config.getPort(), 6667);
        Assert.assertEquals(config.getHttpPort(), 4080);
        Assert.assertEquals(config.getHostName(), "myHostName");
        Assert.assertEquals(config.getKeyStoreFile(), "src/test/resources/keystore");
    }

    @Test
    public void testGetHostnameException() throws IOException {
        new MockUp<InetAddress> () {
            @Mock
            String getHostName() throws UnknownHostException {
                throw new UnknownHostException();
            }
        };
        config = (Config) new Yaml(new Constructor(Config.class)).load(
                Resources.toString(Resources.getResource("config.yml"), StandardCharsets.UTF_8)
        );
    }

    @Test
    public void testParallelInit () throws Exception {
        new MockUp<InetAddress> () {
            @Mock
            String getHostName() throws UnknownHostException {
                return "myHostName";
            }
        };
        Config.init(Config.class.getClassLoader().getResourceAsStream("config.yml"));
        Config config = Config.getInstance();
        Deencapsulation.setField(config, "instance", null);
        Assert.assertNull(config.getInstance());

        final AtomicBoolean completed = new AtomicBoolean(false);
        final AtomicBoolean success = new AtomicBoolean(false);

        synchronized (Config.class) {
            new Thread(() -> {
                try {
                    Config.init(Config.class.getClassLoader().getResourceAsStream("config.yml"));
                    success.set(true);
                } catch (IOException e) {
                    Assert.fail("failed! " + e.getMessage());
                } finally {
                    synchronized (completed) {
                        completed.set(true);
                        completed.notify();
                    }
                }
            }).start();

            Thread.sleep(500);
            Deencapsulation.setField(config, "instance", config);
        }
    }
}