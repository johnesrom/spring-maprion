package io.esrom.maprion.jpa;

import io.esrom.maprion.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@FlatEntity(prefix = "oi")
public class OrderItemDTO {
    @Id
    private UUID uuid;
    private UUID product_uuid;
    private BigDecimal unit_price;

    public UUID getUuid(){ return uuid; }
    public void setUuid(UUID id){ this.uuid = id; }
    public UUID getProduct_uuid(){ return product_uuid; }
    public void setProduct_uuid(UUID p){ this.product_uuid = p; }
    public BigDecimal getUnit_price(){ return unit_price; }
    public void setUnit_price(BigDecimal u){ this.unit_price = u; }
}
