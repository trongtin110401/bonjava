docker-compose -f docker-compose-local.yml down
docker-compose -f docker-compose-local.yml up -d --build
docker-compose -f docker-compose-local.yml logs -f
