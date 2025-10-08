package io.esrom.maprion.jpa;

import io.esrom.maprion.core.MaprionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.datasource.url=jdbc:h2:mem:maprion;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class MaprionEntityManagerIntegrationTest {

    @Autowired EntityManager em;
    @Autowired MaprionJpa maprionJpa;
    @Autowired MaprionMapper mapper;

    @Test
    void mapsOrderGraphFromNativeQuery() {
        UUID orderId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        String sql = """
        SELECT
            o.uuid        AS o_uuid,
            o.order_number AS o_orderNumber,
            o.total        AS o_total,
            o.created      AS o_created,

            crt.uuid       AS crt_uuid,
            crt.name       AS crt_name,

            adr.uuid       AS adr_uuid,
            adr.billing_address AS adr_billing_address,

            oi.uuid        AS oi_uuid,
            oi.product_uuid AS oi_product_uuid,
            oi.unit_price   AS oi_unit_price
        FROM orders o
        LEFT JOIN customers cr      ON cr.uuid = o.customer_uuid
        LEFT JOIN customer_types crt ON crt.uuid = cr.customer_type_uuid
        LEFT JOIN addresses adr     ON adr.uuid = o.address_uuid
        LEFT JOIN order_items oi    ON oi.order_uuid = o.uuid
        WHERE o.uuid = ?1
        """;

        List<OrderDTO> result = maprionJpa.map(em, sql, OrderDTO.class, orderId);

        assertEquals(1, result.size());
        OrderDTO order = result.get(0);
        assertEquals(orderId, order.getUuid());
        assertNotNull(order.getCustomerType());
        assertNotNull(order.getAddress());
        assertEquals(2, order.getItems().size());
    }
}
