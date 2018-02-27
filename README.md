# falcon

This project showcases a Publisher / Consumer application using Spring Boot and Java 8.


### The Problem

POST a JSON payload to a REST endpoint place it somewhere for a consumer to pick it up, 
persist it in a store and push to clients via websocket.

### The Solution

To create a clear boundary between the consumers and producers two services were created,
a gateway service and the documents service.

All ingoing and outgoing messages flow through the gateway. When posting a JSON payload,
a message is published to a RabbitMQ broker, which is picked up by a consumer and saved to
a Mongo database. A group had to be setup on a queue, as to guarantee that the messages would only be delivered to a single 
to only one consumer listening on the queue. 

Once the payload has been stored it's pushed to a MQ fanout exchange,
multicasting the payload to consumers in the gateway and pushed them into the websocket clients.

Additional configuration for RabbitMQ is included in order to prevent consumers/publishers from
automatically configuring the channels. 

## Requirements

- Java 8
- Docker / Docker Composer

## Running the application

Executing the build
```bash
    bin/build.sh
```

Running the application
```bash
    docker-compose up
```

## Endpoints

- Retrieving all posted payloads: GET -> http://localhost:8080/api/documents
- Posting payload: POST -> http://localhost:8080/api/documents
- Websocket endpoint: WS -> ws://localhost:8080/documents
- Client Page: http://localhost:8080/static/index.html