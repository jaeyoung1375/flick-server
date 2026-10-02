#!/bin/bash
docker compose --env-file ../.env -f compose.yml -f compose.local.yml up -d backend redis --no-deps
