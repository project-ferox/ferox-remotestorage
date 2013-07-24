# Ferox - Remotestorage

Ferox-Remotestorage can be run as an embedded or standalone remotestorage server.

# What is Remotestorage?

Remotestorage provides client side applications with a means to read and write data to/from a remote server.  A remotestorage server can be your entire backend.

# How do you use it?

The remotestorage server provides a REST interface for creating, deleting, reading and updating documents.

* Issuing a **PUT** request to `http://example.com/user/documents/todo.json` will create, or update, a document.
* Issuing a **GET** request to `http://example.com/user/documents/todo.json` will read that document.
* Issuing a **DELETE** request to `http://example.com/user/documents/todo.json` will delete that document.
* Issuing a **GET** request to `http://example.com/user/documents/` will return the directory listing in JSON format

Remotestorage also implicitly handles directory management so when creating a new document 
such as `http://example.com/user/documents/school/cs455/homework1.txt`
the Remotestorage server will create any directories that don't yet exist in the path to homework1.txt.


Remotestorage can also handle authentication.  Learn more about [authentication]().


TODO: (PATCH request)
TODO: push state via websockets
TODO: X-Put-Type: File header

# Client Libraries

You could communicate with ferox-remotestorage directly via http requests / xhr but there are also remotestorage client libraries out there to ease the interaction.

* Javascript: https://github.com/remotestorage/remotestorage.js

# Installing Ferox-Remotestorage

The Ferox remotestorage server implementation can be configured in a number of different ways.
We'll cover the default configuration here as it is the quickest configuration to start testing with.

You can read about other configurations on the [wiki](https://github.com/project-ferox/ferox-remotestorage/wiki).

# More
Remotestorage is also a specification so it opens up the possibility for users to store their data with any remotestorage provider they choose.  I.e., if you create a client side application with remotestorage support then users of that application could store their data with your server or some other server that implements the remotestorage protocol.  See http://remotestorage.io


