# Autores
-Oscar Esteban Arias Lopez
-Cesar Ivan Moreno Acero

# Construir imagen vise-api
docker build -t vise-api .

# Correr contenedor
docker run -d -p 8080:8080 --name vise-api-container vise-api

# Entrar al Swagger UI
http://localhost:8080/swagger-ui.html
                  o
http://localhost:8080/swagger-ui/index.html
