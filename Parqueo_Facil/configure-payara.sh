#!/bin/bash

# Iniciar dominio
/opt/payara/bin/asadmin start-domain

# Configurar datasource solo si no existe
if ! /opt/payara/bin/asadmin list-jdbc-connection-pools | grep -q MySQLPool; then
    /opt/payara/bin/asadmin create-jdbc-connection-pool \
    --datasourceclassname=com.mysql.cj.jdbc.MysqlDataSource \
    --restype=javax.sql.DataSource \
    --property="user=root:password=yobay:databaseName=reservas_db:serverName=db:portNumber=3306:useSSL=false" \
    MySQLPool

    /opt/payara/bin/asadmin create-jdbc-resource \
    --connectionpoolid MySQLPool jdbc/reservasDS
fi

# Mantener el contenedor en ejecución
tail -f /opt/payara/glassfish/domains/domain1/logs/server.log