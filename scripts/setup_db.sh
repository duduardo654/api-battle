#!/usr/bin/env bash
# Instala o PostgreSQL na VM (Ubuntu/Debian) e cria o banco/usuário.
set -e
sudo apt-get update
sudo apt-get install -y postgresql
sudo systemctl enable --now postgresql
sudo -u postgres psql <<SQL
DO \$\$ BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'fifa') THEN
    CREATE ROLE fifa LOGIN PASSWORD 'fifa';
  END IF;
END \$\$;
SQL
sudo -u postgres psql -tc "SELECT 1 FROM pg_database WHERE datname='fifa'" | grep -q 1 \
  || sudo -u postgres createdb -O fifa fifa
echo "Banco 'fifa' pronto."
