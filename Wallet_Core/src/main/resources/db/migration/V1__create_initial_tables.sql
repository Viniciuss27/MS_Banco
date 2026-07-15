CREATE TABLE tb_account (
    id BIGSERIAL PRIMARY KEY,
    holder_name VARCHAR(150) NOT NULL,
    document VARCHAR(11) NOT NULL UNIQUE, -- CPF com 11 dígitos
    balance NUMERIC(19, 2) NOT NULL,      -- BigDecimal mapeia para NUMERIC/DECIMAL
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE tb_transaction (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,            --Enum 
    amount NUMERIC(19, 2) NOT NULL,
    balance_after NUMERIC(19, 2) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    
    -- Chave estrangeira
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES tb_account(id)
);