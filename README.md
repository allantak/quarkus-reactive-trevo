<h2 align="center"> 
  🍀 Microserviço Trevo 🍀
</h1>

<p align="center"> API que gerencia os pulverizadores de fertilizantes da indústria trevo. </p>
<p align="center">
  <img height="350" src="https://user-images.githubusercontent.com/61324956/223739972-1cc74f2c-3fb9-46f7-bdae-4079324af744.png" />
</p>

- Objetivo é divulgar um novo portfólio de produtos para os clientes da Indústria Trevo, bem como captar as propostas de interesse nesses produtos. Nesse projeto são divididos dois microserviços product, order e alguns serviços como:

<p align="center">
  <img height="25" src="https://img.shields.io/badge/Keycloak-%2375aadb?style=flat&logo=keycloak&logoColor=white" />
  <img height="25" src="https://img.shields.io/badge/Jaeger-%238a57de?style=flat&logo=jaeger&logoColor=white" />
  <img height="25" src="https://img.shields.io/badge/Kafka-%23000000?style=flat&logo=apache%20kafka&logoColor=white" />
</p>
<p align="center">
  <img height="25" src="https://img.shields.io/badge/Elasticsearch-%234ea94b?style=flat&logo=elasticsearch&logoColor=white" />
  <img height="25" src="https://img.shields.io/badge/Logstash-%234ea94b?style=flat&logo=logstash&logoColor=white" />
  <img height="25" src="https://img.shields.io/badge/Kibana-%234ea94b?style=flat&logo=kibana&logoColor=white" />
</p>

### Getting start


Instalar o ambiente de desenvolvimento

```bash
  docker-compose up -d
```

Execultar Microserviço product e order

```bash
  ./mvnw -f product quarkus:dev 
```

```bash
  ./mvnw -f order quarkus:dev 
```



### Aplicação disponiveis


API Product

```bash
  http://http://localhost:8080/q/swagger-ui/
```

API Order

```bash
  http://http://localhost:8081/q/swagger-ui/
```

Keycloak

```bash
  http://http://localhost:8081/
```

Kibana
16686
```bash
  http://http://localhost:5601/
```

Jaeger

```bash
  http://http://localhost:16686/
```

 
 

