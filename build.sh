#!/usr/bin/env bash

if [ -z "${GPG_PASSPHRASE}" ]; then
  echo "GPG_PASSPHRASE variable should be set";
  exit 1
fi

mvn -DskipTests -Dgpg.passphrase="${GPG_PASSPHRASE}" clean package
