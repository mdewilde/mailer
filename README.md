# SimpleMail

SimpleMail is (yet another) simple Java SMTP email sender.

### Usage example

Create a SimpleMail instance that will send email using localhost:

```Java
Mailer mailer = new Mailer("localhost");
```

Construct an email:

```Java
Mail mail = new Mail()
			.from("m@test.com")
			.addTo("t@test.com")
			.withSubject("Hi")
			.withText("This is your email");
```

Send the email:

```Java
mailer.send(mail);
```

### Maven Central
Include this project directly from Maven Central
```XML
<groupId>be.ceau</groupId>
<artifactId>simplemail</artifactId>
<version>0.9.1</version>
```

###	Source code
Source code is available on [GitHub](https://github.com/mdewilde/simplemail).

### Javadoc
Browse this project's [Javadoc](https://www.ceau.be/simplemail/apidocs/index.html).

### Download
Download this project
* [simplemail-0.9.1-javadoc.jar](https://www.ceau.be/simplemail/simplemail-0.9.1-javadoc.jar)
* [simplemail-0.9.1-javadoc.jar.asc](https://www.ceau.be/simplemail/simplemail-0.9.1-javadoc.jar.asc)
* [simplemail-0.9.1-sources.jar](https://www.ceau.be/simplemail/simplemail-0.9.1-sources.jar)
* [simplemail-0.9.1-sources.jar.asc](https://www.ceau.be/simplemail/simplemail-0.9.1-sources.jar.asc)
* [simplemail-0.9.1.jar](https://www.ceau.be/simplemail/simplemail-0.9.1.jar)
* [simplemail-0.9.1.jar.asc](https://www.ceau.be/simplemail/simplemail-0.9.1.jar.asc)
* [simplemail-0.9.1.pom](https://www.ceau.be/simplemail/simplemail-0.9.1.pom)
* [simplemail-0.9.1.pom.asc](https://www.ceau.be/simplemail/simplemail-0.9.1.pom.asc)

### GnuPG public key
Verify signature files with my [GnuPG public key](https://www.ceau.be/pubkey.gpg).

### License
SimpleMail is licensed under [the Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.txt).