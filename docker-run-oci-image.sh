docker run -d \
  -e SPRING_PROFILES_ACTIVE=devlocal \
  -e SPRING_DATASOURCE_USERNAME=demo \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/demodb \
  --network host \
  cr.demo.com/demo/trafic-processor-app:0.0.1-SNAPSHOT
