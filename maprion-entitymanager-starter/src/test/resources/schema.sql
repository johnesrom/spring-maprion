DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS customer_types;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS addresses;

CREATE TABLE addresses (
  uuid UUID PRIMARY KEY,
  billing_address BOOLEAN
);

CREATE TABLE customer_types (
  uuid UUID PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE customers (
  uuid UUID PRIMARY KEY,
  customer_type_uuid UUID REFERENCES customer_types(uuid)
);

CREATE TABLE orders (
  uuid UUID PRIMARY KEY,
  customer_uuid UUID REFERENCES customers(uuid),
  address_uuid UUID REFERENCES addresses(uuid),
  order_number VARCHAR(50),
  total DECIMAL(12,2),
  created TIMESTAMP
);

CREATE TABLE order_items (
  uuid UUID PRIMARY KEY,
  order_uuid UUID REFERENCES orders(uuid),
  product_uuid UUID,
  unit_price DECIMAL(12,2)
);
