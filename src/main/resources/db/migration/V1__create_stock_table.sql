CREATE TABLE stock (
     stock_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
     book_id INTEGER UNIQUE NOT NULL,
     stock INTEGER NOT NULL,
     created_On TIMESTAMP NOT NULL,
     updated_On TIMESTAMP  NULL
);

CREATE INDEX idx_stock_book_id ON stock(book_id);