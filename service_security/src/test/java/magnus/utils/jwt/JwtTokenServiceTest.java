package magnus.utils.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootTest
class JwtTokenServiceTest {

    @Autowired
    JwtTokenService jwtTokenService;

    @Test
    void test() throws Exception {
        JwtPayload user1 = JwtPayload.builder().allocationTime(Instant.now().toEpochMilli()).userid("1")
                                     .username("user1").authorities(null)
                                     .expireTime(Instant.now().plus(30, ChronoUnit.SECONDS).toEpochMilli()).build();
        String s = jwtTokenService.generateToken(user1);
        System.out.println(s);

        JwtPayload jwtPayload = jwtTokenService.verifyToken(s);
        System.out.println(jwtPayload.getUsername());
    }
}