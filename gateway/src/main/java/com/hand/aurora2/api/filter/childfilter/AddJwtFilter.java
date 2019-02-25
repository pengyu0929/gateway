package com.hand.aurora2.api.filter.childfilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.aurora2.api.filter.HelperFilter;
import com.hand.aurora2.domain.CheckState;
import com.hand.aurora2.domain.RequestContext;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.stereotype.Component;

@Component
public class AddJwtFilter implements HelperFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Signer jwtSigner;

    public AddJwtFilter(Signer jwtSigner) {
        this.jwtSigner = jwtSigner;
    }

    @Override
    public int filterOrder() {
        return 50;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        try {
            String token = objectMapper.writeValueAsString(context.getCustomUserDetails());
            String jwt = "Bearer " + JwtHelper.encode(token, jwtSigner).getEncoded();
            context.response.setJwt(jwt);
            return true;
        } catch (JsonProcessingException e) {
            context.response.setStatus(CheckState.EXCEPTION_GATEWAY_HELPER);
            context.response.setMessage("gateway helper error happened: " + e.toString());
            return false;
        }
    }

}
