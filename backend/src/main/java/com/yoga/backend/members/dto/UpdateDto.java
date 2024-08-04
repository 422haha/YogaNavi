package com.yoga.backend.members.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDto {

    private String password;
    private String nickname;
    private Boolean teacher;
    private Integer authnumber;
    private String imageUrl;
    private String imageUrlSmall;
    private List<String> hashTags;

}
