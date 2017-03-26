package helloworld.rest;

import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by qdnguyen on 3/26/17.
 */
public class RestApplication extends Application {
    private final Set<Object> singletons;

    public RestApplication () {
        final Config c = Config.getInstance();
        final HashSet<Object> s = new HashSet<>();
        s.add(new HelloWorldApi(c));
        singletons = Collections.unmodifiableSet(s);
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
