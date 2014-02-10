
Maven setup
==========

### Declare the Silver Mind repositories in your pom.xml

    <repositories>
        <repository>
            <id>silvermind.release</id>
            <name>Silver Mind Release Repo</name>
            <url>http://maven.silvermind.co/release/</url>
            <releases>
                <checksumPolicy>fail</checksumPolicy>
                <updatePolicy>never</updatePolicy>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
        <repository>
            <id>silvermind.snapshot</id>
            <name>Silver Mind Snapshot Repo</name>
            <url>http://maven.silvermind.co/snapshot/</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>

### Declare jar in your dependencies

    <dependency>
        <groupId>co.silvermind.stripes</groupId>
        <artifactId>stripes-spring</artifactId>
        <version>1.2</version>
    </dependency>

learn more here - http://bgoodin.github.com/stripes-spring/
