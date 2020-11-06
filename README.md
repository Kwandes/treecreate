# Treecreate
> Website-based service for laser engravings in wood and other materials.

### Installation and Deployment

>The program is docker-compatible and that is the suggested deployment method.

#### In order to run the project you need to have two environment variables set on your machine:

- TREECREATE_JDBC_URL - an url pointing to the endpoint of your database, with the username and password included\
*Example:* `jdbc:mysql://username:password@minecraft.net:3306/schemaName?serverTimezone=UTC`

- TREECREATE_QUICKPAY_SECRET - we use quickpay for handling payments. This variable refers to te API key used to authenticate with their API\
- TREECREATE_MAIL_PASS - we use one.com as our mail provider. This the is password used to authenticate with the email account\

*Make sure to you have the environment variables setup with exactly matching names, otherwise the build/run will fail.*

#### How to build and run via a Docker

Navigate over to the project directory and run:
```
docker build -t treecreate-X.X.X --build-arg TREECREATE_JDBC_URL --build-arg TREECREATE_QUICKPAY_SECRET --build-arg TREECREATE_MAIL_PASS .
```
For raspberry Pi and ARM architecture compatibility use **Dockerfile-arm** file
```
docker build -t treecreate-X.X.X --build-arg TREECREATE_JDBC_URL --build-arg TREECREATE_QUICKPAY_SECRET --build-arg TREECREATE_MAIL_PASS -f Dockerfile-arm .
```

This will compile the project and create an image.
The `--build-arg ARG` is used to add an environment variable during building of the project.\
`X-X-X` refers to the version you're running and is optional.

After that you can start the image with:
 ```
 docker run --name treecreate-X.X.X -e TREECREATE_JDBC_URL -e TREECREATE_QUICKPAY_SECRET -e TREECREATE_MAIL_PASS --restart unless-stopped -dp 80:5000 treecreate-X.X.X
```
The port 80 can be changed to whichever port you use to access the website/webserver.\
`--restart unless-stopped` will make sure the container automatically starts after a reboot of the host.

*Hint: Easy deployment without any router port forwarding can be achieved with usage of Dataplicity's Wormhole service*

*Hint 2: For simple HTTPS and reverse proxy, you can use Caddy*

### Technologies Used
- Java 11
- Springboot & Thymeleaf
- SLF4j & Logback
- Maven
- Docker
- JUnit & Selenium WebDriver

### License
This Software is released under an [MIT license](https://opensource.org/licenses/MIT)