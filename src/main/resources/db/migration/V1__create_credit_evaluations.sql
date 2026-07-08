CREATE TABLE IF NOT EXISTS credit_evaluations (
    id UUID PRIMARY KEY,
    identity_card VARCHAR(10) NOT NULL,
    requested_amount NUMERIC(19, 2) NOT NULL,
    evaluated_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_credit_evaluations_identity_card
    ON credit_evaluations (identity_card);

CREATE INDEX IF NOT EXISTS idx_credit_evaluations_evaluated_at
    ON credit_evaluations (evaluated_at DESC);
