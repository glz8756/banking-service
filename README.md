# Banking Service
This project implements RESTful API for a simple banking service.\
It uses Http4s for web service, Doobie for database access and Circe for json parsing.\
It also uses PostgreSQL running in the docker container as a Database.

## How to start Postres database
In order to start the database follow the steps bellow:
1. Navigate to the main project folder
2. Change directory to db: run **cd db** command
3. Start the docker container with database: run **docker compose up** command
4. The database will start up and will create required tables and some dummy data using the default dataset. 

If you need to connect directly to database you can perform the following steps:
1. Run **docker ps** command and note the container id in the first column of the output
2. Run **docker exec -it \<container id\> bash**
3. Run **psql -U docker db_bank**
4. You should be able to run SQL statements at this point.

## Run docker:
1. docker compose up
2. docker exec -it banking bash
3. psql -U docker db_bank
4. \dt

## Run API:
curl -v 'http://localhost:8088/api/account/261389735'

curl -v --header "Content-Type: application/json" \
--request POST \
--data '{ "account_id": "261389735", "amount": 100.00, "description": "Deposit" }' \
http://localhost:8088/api/transaction

curl -v --header "Content-Type: application/json" \
--request POST \
--data '{ "account_id": "261389735", "amount": -50.00, "description": "Withdraw" }' \
http://localhost:8088/api/transaction

curl -v 'http://localhost:8088/api/transaction/history/261389735'




## How to run Web application
* Launch BankingHttpService App on the main project folder  
