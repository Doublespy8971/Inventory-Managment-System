CREATE TABLE product (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         category VARCHAR(50) NOT NULL,
                         price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
                         sku VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE store (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       address VARCHAR(255) NOT NULL
);
CREATE TABLE customer (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          email VARCHAR(100) NOT NULL UNIQUE,
                          phone VARCHAR(15) NOT NULL UNIQUE
);
CREATE TABLE inventory (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           product_id INT NOT NULL,
                           store_id INT NOT NULL,
                           stock_level INT NOT NULL CHECK (stock_level >= 0),

                           FOREIGN KEY (product_id) REFERENCES product(id)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE,

                           FOREIGN KEY (store_id) REFERENCES store(id)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE,

                           UNIQUE (product_id, store_id)
);
CREATE TABLE order_details (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               customer_id INT NOT NULL,
                               store_id INT NOT NULL,
                               total_price DECIMAL(10,2) NOT NULL DEFAULT 0,
                               date DATE NOT NULL,

                               FOREIGN KEY (customer_id) REFERENCES customer(id)
                                   ON DELETE RESTRICT
                                   ON UPDATE CASCADE,

                               FOREIGN KEY (store_id) REFERENCES store(id)
                                   ON DELETE RESTRICT
                                   ON UPDATE CASCADE
);
CREATE TABLE order_item (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            order_id INT NOT NULL,
                            product_id INT NOT NULL,
                            quantity INT NOT NULL CHECK (quantity > 0),
                            price DECIMAL(10,2) NOT NULL CHECK (price >= 0),

                            FOREIGN KEY (order_id) REFERENCES order_details(id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE,

                            FOREIGN KEY (product_id) REFERENCES product(id)
                                ON DELETE RESTRICT
                                ON UPDATE CASCADE
);
INSERT INTO product (name, category, price, sku) VALUES
                                                     ('Galaxy S21', 'Mobile', 799.99, 'SKU001'),
                                                     ('iPhone 13', 'Mobile', 999.99, 'SKU002'),
                                                     ('Samsung QLED TV', 'TV and AV', 1499.99, 'SKU003'),
                                                     ('LG OLED TV', 'TV and AV', 1999.99, 'SKU004'),
                                                     ('Dyson Vacuum Cleaner', 'Home Appliances', 499.99, 'SKU005'),
                                                     ('Dell XPS 13', 'Laptops and Monitors', 1299.99, 'SKU006'),
                                                     ('MacBook Pro 16', 'Laptops and Monitors', 2399.99, 'SKU007'),
                                                     ('Apple Watch Series 7', 'Accessories', 399.99, 'SKU008'),
                                                     ('Sony WH-1000XM4', 'Accessories', 349.99, 'SKU009'),
                                                     ('Apple AirPods Pro', 'Accessories', 249.99, 'SKU010');
INSERT INTO store (name, address) VALUES
                                      ('Tech Store A', '123 Tech Street, CA'),
                                      ('Gadget Hub', '456 Gadget Road, CA'),
                                      ('ElectroMart', '789 Electro Avenue, NY');
INSERT INTO customer (name, email, phone) VALUES
                                              ('John Doe', 'john.doe@example.com', '9000000001'),
                                              ('Jane Smith', 'jane.smith@example.com', '9000000002'),
                                              ('Alice Johnson', 'alice.johnson@example.com', '9000000003'),
                                              ('Bob Brown', 'bob.brown@example.com', '9000000004'),
                                              ('Tom Hanks', 'tom.hanks@example.com', '9000000005');
INSERT INTO inventory (product_id, store_id, stock_level) VALUES
                                                              (1, 1, 50),
                                                              (2, 1, 40),
                                                              (3, 2, 20),
                                                              (4, 2, 15),
                                                              (5, 3, 30),
                                                              (6, 1, 25),
                                                              (7, 2, 10),
                                                              (8, 3, 60),
                                                              (9, 3, 45),
                                                              (10, 1, 70);
INSERT INTO order_details (customer_id, store_id, total_price, date) VALUES
                                                                         (1, 1, 0, '2024-05-10'),
                                                                         (2, 2, 0, '2024-06-12'),
                                                                         (3, 1, 0, '2024-07-03'),
                                                                         (4, 3, 0, '2024-08-21'),
                                                                         (5, 2, 0, '2024-09-15');
INSERT INTO order_item (order_id, product_id, quantity, price) VALUES
                                                                   (1, 1, 1, 799.99),
                                                                   (1, 8, 1, 399.99),

                                                                   (2, 3, 1, 1499.99),

                                                                   (3, 2, 1, 999.99),
                                                                   (3, 10, 2, 249.99),

                                                                   (4, 5, 1, 499.99),

                                                                   (5, 6, 1, 1299.99);
UPDATE order_details od
SET total_price = (
    SELECT SUM(oi.quantity * oi.price)
    FROM order_item oi
    WHERE oi.order_id = od.id
);

