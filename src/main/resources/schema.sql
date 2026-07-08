DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS payment_methods CASCADE;
DROP TABLE IF EXISTS reasons CASCADE;
DROP TABLE IF EXISTS finanzas CASCADE;
DROP TABLE IF EXISTS documento CASCADE;

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,

    -- Contexto
    tenant_id BIGINT NOT NULL,
    people_id BIGINT NOT NULL,

    -- Origen del pago (uno de los dos)
    request_id BIGINT,

    -- Información del pago
    amount NUMERIC(10,2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    reference VARCHAR(150),
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Guardamos los items de libros como JSON texto
    items TEXT,

    -- Estado del pago
    status VARCHAR(30) NOT NULL DEFAULT 'POR CONFIRMAR',
    cancel_reason TEXT,

    -- Auditoría de confirmación
    confirmed_by BIGINT,
    confirmed_at TIMESTAMP,

    -- Auditoría general
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_payment_origin
    CHECK (
        (request_id IS NOT NULL AND items IS NULL)
        OR
        (request_id IS NULL AND items IS NOT NULL)
    )
);

CREATE INDEX idx_payment_people ON payments(people_id);
CREATE INDEX idx_payment_tenant ON payments(tenant_id);
CREATE INDEX idx_payment_request ON payments(request_id);
CREATE INDEX idx_payment_status ON payments(status);
