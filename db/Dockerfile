# =============================================
# Imagen de la base de datos PostgreSQL con el schema incluido
# Build (desde la raiz del proyecto):
#   docker build -f db/Dockerfile -t users-management-db .
# =============================================
FROM postgres:17-alpine

ENV POSTGRES_DB=crud_usuarios

# Los scripts de esta carpeta se ejecutan solo la primera vez (volumen vacio)
COPY src/main/resources/schema.sql /docker-entrypoint-initdb.d/01-schema.sql

EXPOSE 5432
