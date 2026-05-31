-- ================================================
-- Microservicio de Finanzas - FideiNexus
-- Schema SQL: se ejecuta automáticamente al iniciar
-- ================================================

CREATE TABLE IF NOT EXISTS payment_method (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS reason (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    descripcion VARCHAR(255),
    request_id  BIGINT
);

CREATE TABLE IF NOT EXISTS payment (
    id                SERIAL PRIMARY KEY,
    tenant_id         BIGINT NOT NULL,
    people_id         BIGINT NOT NULL,
    monto             NUMERIC(10, 2) NOT NULL,
    fecha_pago        DATE NOT NULL,
    estado            VARCHAR(2) NOT NULL DEFAULT 'P',
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    referencia        VARCHAR(100),
    reason_id         INT REFERENCES reason(id),
    payment_method_id INT REFERENCES payment_method(id)
);

-- Datos semilla: Métodos de pago
INSERT INTO payment_method (id, nombre, descripcion)
VALUES
    (1, 'Yape',                 'Pago mediante app Yape'),
    (2, 'Plin',                 'Pago mediante app Plin'),
    (3, 'Efectivo',             'Pago en efectivo'),
    (4, 'Transferencia Bancaria','Transferencia bancaria directa')
ON CONFLICT (id) DO NOTHING;

-- Datos semilla: Conceptos / Razones de pago
INSERT INTO reason (id, nombre, descripcion, request_id)
VALUES
    (1, 'Fianza por Matrimonio',    'Fianza requerida para tramitar matrimonio', NULL),
    (2, 'Trámite de Certificado',   'Pago por emisión de acta o certificado sacramental', NULL),
    (3, 'Reserva de Misa',          'Pago por reserva de misa parroquial', NULL)
ON CONFLICT (id) DO NOTHING;
