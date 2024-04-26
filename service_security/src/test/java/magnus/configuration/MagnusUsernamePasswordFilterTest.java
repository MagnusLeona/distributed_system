package magnus.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MagnusUsernamePasswordFilterTest {

    @Test
    public void testJacksonTransferStringToMap() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String str = "{\"username\" : \"username\"}";
        Map map = objectMapper.readValue(str, Map.class);
        System.out.println(map);
    }
}