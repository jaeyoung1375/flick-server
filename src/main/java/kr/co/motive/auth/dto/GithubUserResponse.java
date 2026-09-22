package kr.co.motive.auth.dto;

import lombok.Getter;

@Getter
public class GithubUserResponse {

    private Long id;

    private String login;

    private String name;

    private String email;
}
