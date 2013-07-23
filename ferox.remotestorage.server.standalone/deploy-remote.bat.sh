mvn package

scp -r target/runtime/* tantaman@tantaman.com:/home/tantaman/remotestorage-server/

scp target/runtime/configurations/services/ferox.remotestorage.route_initializer.properties.production tantaman@tantaman.com:/home/tantaman/remotestorage-server/configurations/services/ferox.remotestorage.route_initializer.properties