CREATE TABLE stock_request_history (
       stock_request_history_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
       stockRequested INTEGER NOT NULL,
       created_On TIMESTAMP NOT NULL,
       stock_id INTEGER NOT NULL,
       CONSTRAINT fk_stock_request_history_stock_id FOREIGN KEY(stock_id) REFERENCES stock(stock_id)
);