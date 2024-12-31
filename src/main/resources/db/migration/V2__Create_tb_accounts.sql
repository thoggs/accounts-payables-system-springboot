CREATE TABLE tb_accounts
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    due_date     DATE           NOT NULL,
    payment_date DATE,
    amount       NUMERIC(10, 2) NOT NULL,
    description  VARCHAR(255)   NOT NULL,
    status       VARCHAR(20)    NOT NULL,
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'PAID'))
);
