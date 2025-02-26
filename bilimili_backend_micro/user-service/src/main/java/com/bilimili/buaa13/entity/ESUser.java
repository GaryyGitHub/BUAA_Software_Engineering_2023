package com.bilimili.buaa13.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//用于在elastic中的搜索，只有昵称和uid
public class ESUser {
    private Integer uid;
    private String nickname;
}
