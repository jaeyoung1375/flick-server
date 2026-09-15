package kr.co.flick.auth.dto;

import lombok.Getter;

@Getter
public class GoogleUserResponse {

    private String sub;

    private String email;

    private String name;
}
