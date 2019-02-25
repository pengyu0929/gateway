package com.hand.aurora2.api.service;

import com.hand.aurora2.domain.CustomUserDetailsWithResult;

public interface GetUserDetailsService {
    CustomUserDetailsWithResult getUserDetails(String accessToken);
}
