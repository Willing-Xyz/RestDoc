package cn.willingxyz.restdoc.spring.filter;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

/**
 * @author cweijan
 * @version 2019/11/2/002 19:34
 */
public class HttpBasicAuthFilter implements Filter {
    private String username;
    private String password;
    private boolean enable;

    public HttpBasicAuthFilter(String username, String password) {
        this.enable = username != null && password != null;
        this.username = username;
        this.password = password;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!enable) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String authorization = httpServletRequest.getHeader("Authorization");
        byte[] encode = ENCODER.encode((username + ":" + password).getBytes());
        httpServletResponse.addHeader("WWW-authenticate", "Basic Realm=\"RestDoc\"");
        if (!("Basic " + new String(encode)).equals(authorization)) {
            httpServletResponse.setStatus(401);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }

}
