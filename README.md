# lwm2m-Client
## A standalone LWM2M client
- To be used with https://github.com/LuisV/Server-Dashboard

Simply run with maven to grab all the dependencies

The client will try to look for a LWM2M server endpoint at `coap://localhost:5683`

If you would like the client to be secure, instead connect to `coap://localhost:5684` and add the identity and key to the client
