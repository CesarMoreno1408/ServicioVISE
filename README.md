
# Autores
-Oscar Esteban Arias Lopez
-Cesar Ivan Moreno Acero

# Construir imagen vise-api
docker build -t vise-api .

# Correr contenedor
docker run -d -p 8080:8080 --name vise-api-container vise-api

# Entrar al Swagger UI
http://localhost:443/swagger-ui.html
                  o
http://localhost:443/swagger-ui/index.html
=======
## 👥 Colaboradores

- [@CesarMoreno1408](https://github.com/CesarMoreno1408)
- [@Oscararias03](https://github.com/Oscarias03)
