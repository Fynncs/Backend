import br.com.fynncs.core.model.Resource;
import br.com.fynncs.services.ReaderEncryptedProperties;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ResourcesTest {

    @Test
    public void encryptDataBaseConnection() throws Exception {
        ReaderEncryptedProperties properties = new ReaderEncryptedProperties();
        properties.read(properties.path("resource.properties", Resource.class));
        for (Map.Entry<Object, Object> entry : properties.getProperties().entrySet()) {
            properties.addEncryptProperties((String) entry.getKey(), (String) entry.getValue());
        }
        String urlConnection = "jdbc:postgresql://url/database?user=&password=";
        properties.addEncryptProperties("data-base.postgres.url.connection", urlConnection);
        properties.save(properties.path("resource.properties", Resource.class), properties.getProperties());
    }
}
