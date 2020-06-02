package softuni.workshop.domain.models.service;

import softuni.workshop.domain.entities.User;

public class FileServiceModel {
    private Integer id;
    private String name;
    private UserServiceModel user;

    public FileServiceModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserServiceModel getUser() {
        return user;
    }

    public void setUser(UserServiceModel user) {
        this.user = user;
    }
}
