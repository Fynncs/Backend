import br.com.fynncs.services.ReaderEncryptedProperties;
import org.junit.jupiter.api.Test;

public class ResourcesTest {

    @Test
    public void encryptDataBaseConnection() throws Exception {
        ReaderEncryptedProperties properties = new ReaderEncryptedProperties();
        properties.read("src/main/resources/application.properties");
        String urlConnection = "";
        properties.addEncryptProperties("data-base.postgres.url.connection", urlConnection);
        properties.save("src/main/resources/application.properties", properties.getProperties());
    }
}
