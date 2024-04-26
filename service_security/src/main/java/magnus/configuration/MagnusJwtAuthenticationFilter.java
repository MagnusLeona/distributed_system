package magnus.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import lombok.NonNull;
import lombok.Setter;
import magnus.utils.jwt.JwtPayload;
import magnus.utils.jwt.JwtTokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Setter
public class MagnusJwtAuthenticationFilter extends OncePerRequestFilter {

    private String tokenHeader;
    private ObjectMapper objectMapper;
    private JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException, ServletException {
        if (requireAuthentication(request)) {
            // 如果是POST请求，则获取对应的token报文头
            String header = request.getHeader(tokenHeader);
            if (header != null) {
                try {
                    // 验证成功
                    JwtPayload jwtPayload = jwtTokenService.verifyToken(header);
                    // 直接放行
                    UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(
                            jwtPayload.getUserid(), jwtPayload.getPassword(), jwtPayload.getAuthorities());
                    // todo 查询出用户相关信息，放到Authentication里面
                    SecurityContextHolder.getContext().setAuthentication(authenticated);
                } catch (Exception e) {
                    // 校验失败，交给后续的步骤来校验
                    response.setHeader(tokenHeader, null);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    public boolean requireAuthentication(HttpServletRequest request) {
        if (request.getHeader(tokenHeader) != null) {
            return true;
        }
        return false;
    }

    private void errorHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 报错直接返回登录异常
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Map<String, Object> map = new HashMap<>();
        map.put("result", "登录失败");
        response.getWriter().write(objectMapper.writeValueAsString(map));
    }
}
