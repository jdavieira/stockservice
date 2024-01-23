ALTER TABLE  IF EXISTS  stock_request_history
ADD COLUMN user_email VARCHAR(100) null;


ALTER TABLE  IF EXISTS  stock_request
ADD COLUMN user_email VARCHAR(100) null;