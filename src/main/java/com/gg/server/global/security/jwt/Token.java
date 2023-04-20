package com.gg.server.global.security.jwt;

import com.gg.server.domain.user.User;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER_REFRESH_TOKEN")
public class Token {
    @Id
    @Column(name = "refresh_token_seq")
    @NotNull
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long refreshTokenSeq;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "intra_id")
    private User user;

    @Column(name = "refresh_token", length = 256)
    @NotNull
    private String refreshToken;

    @Column(name = "access_token", length = 256)
    @NotNull
    private String accessToken;

    public Token(
            @NotNull User user,
            @NotNull String refreshToken,
            @NotNull String accessToken
    ) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}