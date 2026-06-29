
-- Reset all microservice databases (safe drop if they already exist).
DROP DATABASE IF EXISTS ecomv1_order_db; DROP DATABASE IF EXISTS ecomv1_payment_db; DROP DATABASE IF EXISTS ecomv1_product_db; DROP DATABASE IF EXISTS ecomv1_user_db;

-- Recreate fresh databases for order, payment, product, and user services.
CREATE DATABASE ecomv1_order_db; CREATE DATABASE ecomv1_payment_db; CREATE DATABASE ecomv1_product_db; CREATE DATABASE ecomv1_user_db;

-- Query to view row counts for each table in the ecom databases.
USE ecomv1_product_db; SHOW TABLES; SELECT 'products' AS table_name, COUNT(*) AS row_count FROM products; SELECT 'inventory' AS table_name, COUNT(*) AS row_count FROM inventory;USE ecomv1_order_db; SHOW TABLES; SELECT 'orders' AS table_name, COUNT(*) AS row_count FROM orders; SELECT 'order_items' AS table_name, COUNT(*) AS row_count FROM order_items;USE ecomv1_payment_db; SHOW TABLES; SELECT 'payments' AS table_name, COUNT(*) AS row_count FROM payments;USE ecomv1_user_db; SHOW TABLES; SELECT 'users' AS table_name, COUNT(*) AS row_count FROM users;