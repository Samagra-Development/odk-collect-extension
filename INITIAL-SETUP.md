# Inital Setup

Initial setup of the project will be divided into 2 main steps

* Setting up the ODK Central Server on your local machine
* Setting up the Android app in Android Studio
---
## Setting up the ODK Central Server on your local machine

For installing the ODK Central server locally on your machine, Make sure you are running **Docker Engine v23.x** and **Docker Compose v2.16.x** or greater.

```
docker --version && docker compose version
```
 <br> 

## Setting Up Central
 <br> 

* Download the software.

    ```
    git clone https://github.com/getodk/central
    ```
* Go into the new central folder:
    ```
    cd central
    ```
* Get the latest client and server:
    ```
    git submodule update -i
    ```
* Update settings.
    ```
    cp .env.template .env
    nano .env
    ```
* Make the following changes in `.env` file
  * Change the ``Domain`` value to your machine's local IP address. For Eg. DOMAIN=192.168.29.147.
  * You can check your ip by running the command `ifconfig`. If you're using ethernet, it should probably be named similar to `eth*`; if you're on WLAN network, it should be `wlo*`.
  * Change the ``SSL_TYPE`` value to selfsign
  * Hold Ctrl and press x to quit the text editor. Press y to indicate that you want to save the file, and then press Enter to confirm the file name. Do not change the file name.

* Let the system know that you want the latest version of the database

    ```
    touch ./files/allow-postgres14-upgrade
    ```

---

If you are on macOS you need to do the following additional changes:

* Inside the central folder, open `docker-compose.yml`
* Inside the `service volumes` you need to edit the `/data/transfer` line <br>
  Your `service` should look like this:

    ```Dockerfile
    service:
    build:
      context: .
      dockerfile: service.dockerfile
    depends_on:
      - secrets
      - postgres14
      - mail
      - pyxform
      - enketo
    volumes:
      - secrets:/etc/secrets
      - $PWD/data/transfer:/data/transfer
    environment:
      - DOMAIN=${DOMAIN}
      - SYSADMIN_EMAIL=${SYSADMIN_EMAIL}
      - HTTPS_PORT=${HTTPS_PORT:-443}
      - NODE_OPTIONS=${SERVICE_NODE_OPTIONS:-''}
      - DB_HOST=${DB_HOST:-postgres14}
      - DB_USER=${DB_USER:-odk}
      - DB_PASSWORD=${DB_PASSWORD:-odk}
      - DB_NAME=${DB_NAME:-odk}
      - DB_SSL=${DB_SSL:-null}
      - EMAIL_FROM=${EMAIL_FROM:-no-reply@${DOMAIN}}
      - EMAIL_HOST=${EMAIL_HOST:-mail}
      - EMAIL_PORT=${EMAIL_PORT:-25}
      - EMAIL_SECURE=${EMAIL_SECURE:-false}
      - EMAIL_IGNORE_TLS=${EMAIL_IGNORE_TLS:-true}
      - EMAIL_USER=${EMAIL_USER:-''}
      - EMAIL_PASSWORD=${EMAIL_PASSWORD:-''}
      - SENTRY_ORG_SUBDOMAIN=${SENTRY_ORG_SUBDOMAIN:-o130137}
      - SENTRY_KEY=${SENTRY_KEY:-3cf75f54983e473da6bd07daddf0d2ee}
      - SENTRY_PROJECT=${SENTRY_PROJECT:-1298632}
    command: [ "./wait-for-it.sh", "${DB_HOST:-postgres14}:5432", "--", "./start-odk.sh" ]
    restart: always
    logging:
      driver: local
    ```

---
* Now you will need to bundle everyting together. This may take some time

    ```
    docker compose build
    ```
  When it finishes, you should see some "Successfully built" type text. Make sure that you do not have any service running on your local port 80.

* Start the server you just created. If its the first time its going to take a while to get ready

    ```
    docker compose up -d
    ```
* Check whether ODK has finished loading.

    ```
    docker compose ps
    ```
  Under the Status column, for the ```central-nginx-1``` row, you will want to see text that reads Up or Up (healthy). If you see Up (health: starting), give it a few minutes. If you see some other text, something has gone wrong.

Now You can check if everything is working by putting your local machine's IP in the browser.

* Now you need to create an account. Make sure you are in the ```central``` directory before entering this command

    ```
    docker compose exec service odk-cmd --email YOUREMAIL@ADDRESSHERE.com user-create
    ```
* Make the account an administrator
    ```
    docker compose exec service odk-cmd --email YOUREMAIL@ADDRESSHERE.com user-promote
    ```
  Log into the Central website. Go to your domain name and enter in your new credentials. Once you have one administrator account, you do not have to go through this process again for future accounts: you can log into the website with your new account, and directly create new users that way.

Congratulations now you have a working ODK Central server.

---

## Securing the local endpoints
<br>

Now you need to create SSL certificates and add them to your endpoints. By doing this you are making your local https endpoints secure. We will use ``mkcert`` to create self-signed certificates.

To do so follow these steps:

* First make sure you have ``mkcert`` installed. If not follow these [instructions](https://github.com/FiloSottile/mkcert#installation) for installing mkcert on your operating system.

* Add mkcert to your local root CAs. In your terminal, run the following command:

    ```
    mkcert -install
    ```
  This generates a local certificate authority (CA). Your mkcert-generated local CA is only trusted locally, on your device.

* Generate a certificate for your site, signed by mkcert. Create a new folder named `ssl-certs` in the central directory. In the terminal, navigate to this newly created folder. Then run:

    ```
    mkcert [Your Local Machine's IP]
    ```
  For Eg. run the following command: `mkcert 192.168.29.147`. This should create the certificate and the key inside the folder.

Now you will need to edit some files related to docker in order to use this newly created SSL certificate.

* Inside the central folder, open `docker-compose.yml`
* Add the following volume to `nginx`.
  Your nginx should look like this:
    ```DockerFile
    nginx:
    build:
      context: .
      dockerfile: nginx.dockerfile
    depends_on:
      - service
      - enketo
    environment:
      - DOMAIN=${DOMAIN}
      - CERTBOT_EMAIL=${SYSADMIN_EMAIL}
      - SSL_TYPE=${SSL_TYPE:-letsencrypt}
      - SENTRY_ORG_SUBDOMAIN=${SENTRY_ORG_SUBDOMAIN:-o130137}
      - SENTRY_KEY=${SENTRY_KEY:-3cf75f54983e473da6bd07daddf0d2ee}
      - SENTRY_PROJECT=${SENTRY_PROJECT:-1298632}
    ports:
      - "${HTTP_PORT:-80}:80"
      - "${HTTPS_PORT:-443}:443"
    volumes:
      - $PWD/ssl-certs:/etc/sslcerts
    healthcheck:
      test: [ "CMD-SHELL", "nc -z localhost 80 || exit 1" ]
    restart: always
    logging:
      driver: local
      options:
        max-file: "30"
    ```

* Now you need to edit the `odk.conf.template` file. You should find this file in `central/files/nginx`.<br>
  Change the following code inside server to this:
    ```
    ssl_certificate /etc/sslcerts/[Your Local Machine's IP].pem;
    ssl_certificate_key /etc/sslcerts/[Your Local Machine's IP]-key.pem;
    ssl_trusted_certificate /etc/sslcerts/[Your Local Machine's IP].pem;
    ```
  Basically we are attatching the certificates we created earlier to the server endpoints.

Now you can just delete the previous docker images and containers you created and create new images and containers using the updated code. Previous build was just to see that your server was successfully built or not. If everything goes as planned you should be able to connect to the server using your browser securely.

---

## Setting up the Android app in Android Studio

Congratulations!! Now your ODK Central server is up and ready. Now you need to connect your app to the server.

* Clone the app repo and open the project in android studio. You should see a gradle error. To resolve this error add the following code to `local.properties`

    ```
    sonatypeStagingProfileId=""
    ossrhUsername=""
    ossrhPassword=""
    signing.keyId=""
    signing.key=""
    signing.password=""
    ```
* Next you need to create the `settings.json`. create this file inside the sample/src/main/res/raw. Use [this](https://docs.getodk.org/collect-import-export/#list-of-keys-for-all-settings) to create this file.

* For the `server-url`. Follow these instructions:

  * open your server -> create a project -> create a user create a form inside the project
  * go to form access tab check the checkbox on your created user
  * go to user access tab, you'll see that there is a generate QR code option you need to scan this QR on odk collect app (available on playstore) once you scan this QR you'll setup a project on odk collect app.
  * Go to settings and then project management. You'll see a server url there
  * copy that url and paste it in your settings file. Url should look something like this https://192.169.29.147:443/v1/key/jP5eczJBZq08ECf9OKoNJGwjx55wBa3cpHkJ0r5dPQYEXcXM8uzBOTi38E9NPX$t/projects/2. <br>
    Instead of 192.169.29.147 it should be your local machine's IP

Now you need to add the CA you previously created to your android emulator so that the device knows its a trusted CA.

* Find the location of the root CA you created earlier. Run the following command in the terminal.

    ```
     echo "$(mkcert -CAROOT)/rootCA.pem"
    ```
* Now put this file inside your emulator

* Now install this CA certificate on your emulator.
  To do this<br>
  * Open settings
  * Go to 'Security'
  * Go to 'Encryption & Credentials'
  * Go to 'Install from storage'
  * Select 'CA Certificate' from the list of types available
  * Accept a large scary warning
  * Browse to the certificate file on the device and open it
  * Confirm the certificate install



After All this you should be able to run the app successfully.