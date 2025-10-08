package io.esrom.maprion.jpa;

import io.esrom.maprion.annotation.*;

import java.util.UUID;

@FlatEntity(prefix = "crt")
public class CustomerTypeDTO {
    @Id
    private UUID uuid;
    private String name;

    public UUID getUuid(){ return uuid; }
    public void setUuid(UUID id){ this.uuid = id; }
    public String getName(){ return name; }
    public void setName(String n){ this.name = n; }
}
