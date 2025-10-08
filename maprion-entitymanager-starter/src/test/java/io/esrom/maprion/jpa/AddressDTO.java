package io.esrom.maprion.jpa;

import io.esrom.maprion.annotation.*;

import java.util.UUID;

@FlatEntity(prefix = "adr")
public class AddressDTO {
    @Id
    private UUID uuid;
    private Boolean billing_address;

    public UUID getUuid(){ return uuid; }
    public void setUuid(UUID id){ this.uuid = id; }
    public Boolean getBilling_address(){ return billing_address; }
    public void setBilling_address(Boolean b){ this.billing_address = b; }
}
