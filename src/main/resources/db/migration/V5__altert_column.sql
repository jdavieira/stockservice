ALTER TABLE  IF EXISTS  stock_request_history
ADD COLUMN stock_requested INTEGER NULL;

ALTER TABLE IF EXISTS  stock_request
ADD COLUMN stock_requested INTEGER NULL;