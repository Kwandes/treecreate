# Treecreate
> Website-based service for laser engravings in wood and other materials.

### Installation and Deployment

>The program is docker-compatible and that is the suggested deployment method.

Navigate over to the program directory and run:
```
docker build -t treecreate-X.X.X --build-arg TREECREATE_JDBC_URL .
```
For raspberry Pi and ARM architecture compatibility use **Dockerfile-arm** file
```
docker build -t treecreate-X.X.X --build-arg TREECREATE_JDBC_URL -f Dockerfile-arm .
```

This will compile the project and create an image.
The `--build-arg TREECREATE_JDBC_URL` is used to add an environment variable during building of the project.

After that you can start the image with:
 ```
 docker run --name treecreate-X.X.X -e TREECREATE_JDBC_URL -dp 80:5000 treecreate-X.X.X
```
The port 80 can be changed to whichever port you use to access the website.
Hint: Easy deployment without any router port forwarding can be achieved with usage of Dataplicity's Wormhole service

*Make sure to you have the environment variables setup with exactly matching names, otherwise the build/run will fail.*
*The url follow a format of*

`jdbc:mysql://username:password@minecraft.net:3306/schemaName?serverTimezone=UTC`

### Technologies Used
- Java 11
- Springboot
- SLF4j & Logback
- Maven

### License
This Software is released under an [MIT license](https://opensource.org/licenses/MIT)