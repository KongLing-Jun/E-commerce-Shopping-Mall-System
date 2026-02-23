#!/bin/sh
set -e

CERT_DIR="/etc/nginx/ssl"
CERT_FILE="${CERT_DIR}/fullchain.pem"
KEY_FILE="${CERT_DIR}/privkey.pem"
SSL_CN="${SSL_CN:-localhost}"
SSL_DAYS="${SSL_DAYS:-3650}"

if [ ! -f "${CERT_FILE}" ] || [ ! -f "${KEY_FILE}" ]; then
  echo "[nginx] generating self-signed certificate for CN=${SSL_CN}"
  mkdir -p "${CERT_DIR}"
  openssl req -x509 -nodes -newkey rsa:2048 \
    -days "${SSL_DAYS}" \
    -subj "/CN=${SSL_CN}" \
    -keyout "${KEY_FILE}" \
    -out "${CERT_FILE}" >/dev/null 2>&1
fi
