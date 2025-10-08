# Maprion (EntityManager)

Biblioteca para mapear resultados *flat* (JOINs) obtidos via **EntityManager** (`createNativeQuery(..., Tuple.class)`) em **objetos hierárquicos** com sub-objetos e coleções.

## Módulos
- **maprion-core**: núcleo (anotações, mapeador genérico baseado em RowSource)
- **maprion-entitymanager-starter**: auto-configuração Spring Boot e adaptador JPA (`MaprionJpa`)

## Instalação local
```bash
cd maprion-entitymanager
mvn clean install
```

## Como usar no seu projeto
Adicione o starter:
```xml
<dependency>
  <groupId>io.esrom.maprion</groupId>
  <artifactId>maprion-entitymanager-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

Crie seus DTOs anotados:
```java
@FlatEntity(prefix = "o")
class OrderDTO {
  @Id UUID uuid;
  @Nested(prefix = "crt") CustomerTypeDTO customerType;
  @Nested(prefix = "adr") AddressDTO address;
  @OneToMany(prefix = "oi", elementType = OrderItemDTO.class, idColumn = "uuid")
  List<OrderItemDTO> items = new ArrayList<>();
}
```

Execute a query nativa com aliases prefixados e mapeie:
```java
@Autowired EntityManager em;
@Autowired MaprionJpa maprionJpa;

List<OrderDTO> orders = maprionJpa.map(em, SQL, OrderDTO.class, param1, param2);
```

Veja **tests** no módulo `maprion-entitymanager-starter` para um exemplo completo com H2.
