#!/bin/sh

mvn install

ssh tantaman@tantaman.com 'cd /home/tantaman/remotestorage-server; ./clean.sh'

# Copy jars
scp -r target/runtime/* tantaman@tantaman.com:/home/tantaman/remotestorage-server/

# Copy production configurations over and rename them.
cd target/runtime
ls configurations/services/*.production | sed s/\.production$// | xargs -i% sh -c 'scp %.production tantaman@tantaman.com:/home/tantaman/remotestorage-server/%'
