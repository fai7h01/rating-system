FROM quay.io/keycloak/keycloak:legacy

ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin

CMD ["-Dkeycloak.http.port=8080"]
