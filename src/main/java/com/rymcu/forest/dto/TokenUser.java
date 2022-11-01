package com.rymcu.forest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Set;

/**
 * @author ronger
 */
@Data
public class TokenUser {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long idUser;

    private String account;

    private String nickname;

    private String token;

    private String avatarUrl;

    private String refreshToken;

    private Set<String> scope;

    private String bankAccount;

}
