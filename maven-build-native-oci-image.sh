export BUILDPACK_CACHE_NAME=localCache
export CONTAINER_IMAGE_REGISTRY='cr.demo.com'
export CONTAINER_IMAGE_GROUP='demo'
export CONTAINER_IMAGE_TAG=0.0.1-SNAPSHOT
export CONTAINER_IMAGE_TIMESTAMP='now'
export CONTAINER_IMAGE_PUBLISH='false'

./mvnw \
    -DskipTests=true \
    -Dbuildpack.buildCache.name=$BUILDPACK_CACHE_NAME \
    -Dbuildpack.launchCache.name=$BUILDPACK_CACHE_NAME \
    -Dcontainer.image.registry=$CONTAINER_IMAGE_REGISTRY \
    -Dcontainer.image.group=$CONTAINER_IMAGE_GROUP \
    -Dcontainer.image.tag=$CONTAINER_IMAGE_TAG \
    -Dcontainer.image.timestamp=$CONTAINER_IMAGE_TIMESTAMP \
    -Dcontainer.image.publish=$CONTAINER_IMAGE_PUBLISH \
    -Pbuild-native-oci-image \
    --batch-mode \
    clean package



