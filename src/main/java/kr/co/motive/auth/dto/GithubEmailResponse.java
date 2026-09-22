package kr.co.motive.auth.dto;

import lombok.Getter;

@Getter
public class GithubEmailResponse {

    private String email;

    private boolean primary;

    private boolean verified;
}
