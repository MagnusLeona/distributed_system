package magnus.utils.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import magnus.resources.exception.InvalidTokenException;
import magnus.resources.exception.TokenExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.time.Instant;

@Slf4j
@Component
@ConfigurationProperties(prefix = "magnus.jwt")
public class JwtTokenService {

    private MACSigner macSigner;
    private MACVerifier macVerifier;
    private String secret;

    @Autowired
    ObjectMapper objectMapper;

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String generateToken(JwtPayload jwtPayload) {
        try {
            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
            Payload payload = new Payload(objectMapper.writeValueAsString(jwtPayload));
            JWSObject jwsObject = new JWSObject(jwsHeader, payload);
            jwsObject.sign(macSigner);
            return jwsObject.serialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JwtPayload verifyToken(String token) throws Exception {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            if (!jwsObject.verify(macVerifier)) {
                throw new InvalidTokenException();
            }
            String payloadStr = jwsObject.getPayload().toString();
            JwtPayload jwtPayload = objectMapper.readValue(payloadStr, JwtPayload.class);
            if (jwtPayload.getExpireTime() == null || jwtPayload.getExpireTime() < Instant.now().toEpochMilli()) {
                throw new TokenExpiredException();
            }
            return jwtPayload;
        } catch (Exception e) {
            log.error("wrong token found");
        }
        return null;
    }

    @PostConstruct
    public void postConstruct() throws JOSEException {
        assert secret != null;
        String hex = DigestUtils.md5DigestAsHex(secret.getBytes());
        macSigner = new MACSigner(hex);
        macVerifier = new MACVerifier(hex);
    }
}
