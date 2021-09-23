package org.example.app.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserWithRole {
    private long id;
    private String username;
    private String role;
}
