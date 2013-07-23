#!/bin/sh

mvn install

cd ../felix
./clean.sh

cd ../ferox.remotestorage.server.standalone

cp -r target/runtime/* ../felix
