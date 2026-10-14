-- =============================================
-- Script de creación de la base de datos (PostgreSQL)
-- Gestión de Usuarios - Arquitectura Hexagonal
-- =============================================
-- Ejecutar conectado a la base de datos crud_usuarios.
-- Si no existe, créela antes con:
--   CREATE DATABASE crud_usuarios ENCODING 'UTF8';
-- (En Docker la crea automáticamente la variable POSTGRES_DB)

CREATE TABLE IF NOT EXISTS users (
    id          VARCHAR(36)  NOT NULL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL
                CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'MEMBER', 'REVIEWER')),
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'PENDING', 'BLOCKED')),
    -- updated_at lo actualiza la aplicación en cada UPDATE (Postgres no soporta ON UPDATE)
    created_at  TIMESTAMP(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP(0) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Cuentas: al crear un usuario se abre automaticamente su cuenta SAVINGS
CREATE TABLE IF NOT EXISTS accounts (
    id          VARCHAR(36)  NOT NULL PRIMARY KEY,
    user_id     VARCHAR(36)  NOT NULL,
    owner_name  VARCHAR(100) NOT NULL,
    owner_email VARCHAR(150) NOT NULL,
    type        VARCHAR(20)  NOT NULL
                CONSTRAINT chk_accounts_type CHECK (type IN ('SAVINGS', 'CHECKING')),
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
                CONSTRAINT chk_accounts_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED')),
    created_at  TIMESTAMP(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_accounts_user_type UNIQUE (user_id, type)
);

-- Usuario administrador inicial (password: Admin1234!)
INSERT INTO users (id, name, email, password, role, status)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Administrador',
    'admin@example.com',
    '$2a$12$placeholderHashReplaceWithRealBCryptHash',
    'ADMIN',
    'ACTIVE'
)
ON CONFLICT (id) DO NOTHING;
