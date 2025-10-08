package io.esrom.maprion.jpa;

import io.esrom.maprion.annotation.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@FlatEntity(prefix = "o")
public class OrderDTO {
    @Id
    private UUID uuid;

    private String orderNumber;
    private BigDecimal total;
    private Instant created;

    @Nested(prefix = "crt")
    private CustomerTypeDTO customerType;

    @Nested(prefix = "adr")
    private AddressDTO address;

    @OneToMany(prefix = "oi", elementType = OrderItemDTO.class, idColumn = "uuid")
    private List<OrderItemDTO> items = new ArrayList<>();

    public UUID getUuid(){ return uuid; }
    public void setUuid(UUID id){ this.uuid = id; }
    public String getOrderNumber(){ return orderNumber; }
    public void setOrderNumber(String s){ this.orderNumber = s; }
    public BigDecimal getTotal(){ return total; }
    public void setTotal(BigDecimal t){ this.total = t; }
    public Instant getCreated(){ return created; }
    public void setCreated(Instant i){ this.created = i; }
    public CustomerTypeDTO getCustomerType(){ return customerType; }
    public void setCustomerType(CustomerTypeDTO c){ this.customerType = c; }
    public AddressDTO getAddress(){ return address; }
    public void setAddress(AddressDTO a){ this.address = a; }
    public List<OrderItemDTO> getItems(){ return items; }
    public void setItems(List<OrderItemDTO> it){ this.items = it; }
}
