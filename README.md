# Soundplanes
Project for 2019-20 AISS course

http://soundplanes.appspot.com/

Soundplanes tries to connect people in an innovative way: traveling all around the world!

You can land on everybody else's airports, chat with people there, and listen to the same music, just at the same time as they do!

Awesome stuff made by:

- Bogdan, George Laurentiu
- Diz Gil, Guillermo
- Muñoz Pérez, Carmen María
- Rodríguez Pérez, Francisco

# Running locally
In order to run the server locally, some requirements are needed first. Follow these steps:
1. Run the **Google DataStore Emulator**. If you don't have it installed on your computer, see https://cloud.google.com/datastore/docs/tools/datastore-emulator. By default the emulator runs at http://localhost:8081/. Change this if necessary.
2. Go to the project src folder and open **aiss.model.listener.ObjectifyListener**. Make sure that **PRODUCTION** is set as **FALSE** and change **DATASTORE_EMULATOR_URL** if needed so it matchs the address where your emulator is running.
3. Launch the **AppEngine devserver** (by default, at http://localhost:8090/)
4. Open the **lighttpd-proxy** folder and run lighttpd.exe. This is needed for enabling HTTPS connections with a local, self-trusted certificate, so the client can request access to the Geolocation API.
5. The lighttpd proxy will listen at **https://localhost/** (port 443) and **http://localhost/** (port 80), though this one will be redirected to HTTPS. Enjoy!

# Deploying
Just open **aiss.model.listener.ObjectifyListener** and make sure that PRODUCTION is set as TRUE. Then you can deploy the project normally.