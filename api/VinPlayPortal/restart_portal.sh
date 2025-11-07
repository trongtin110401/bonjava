#!/bin/bash

# Dừng container cũ
docker compose down

# Build và chạy lại container
docker compose up -d --build

# Xem log
docker compose logs -f
