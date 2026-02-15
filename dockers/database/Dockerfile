FROM postgres:17.8

ENV POSTGRES_USER postgres
ENV POSTGRES_PASSWORD postgres

ADD scripts/01_init.sql /docker-entrypoint-initdb.d

CMD ["postgres", "-c", "log_statement=all", "-c", "log_destination=stderr"]
