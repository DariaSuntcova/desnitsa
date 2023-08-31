package ru.desnitsa.backend.entities;

import org.springframework.data.annotation.Id;

public class Role {
    @Id
    Long id;
    Long userId;
    String name;

    public Role(Long id, Long userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
    }

    public static Role of(String name) {
        return new Role(null, null, name);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
