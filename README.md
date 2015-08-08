# SimpleMail

SimpleMail is (yet another) simple Java SMTP email sender.

### Usage example

Create a Mailer instance to send email using localhost:

```Java
Mailer mailer = new Mailer("localhost");
```

Construct an email:

```Java
Mail mail = new Mail()
			.from("from@ceau.be")
			.to("to@ceau.be")
			.bcc("bcc@ceau.be")
			.withSubject("'tis but a subject")
			.withText("o hi there");
```

Send the email:

```Java
mailer.send(mail);
```

It is also possible to construct a Mailer instance with a fallback value for the from field, and additional addresses to be added as to, cc and/or bcc of every sent email.

```Java
Mailer mailer = Mailer.builder("localhost")
				.from("application@ceau.be")
				.cc("cc@ceau.be")
				.build();
```

### Maven Central
Include this project directly from Maven Central
```XML
<groupId>be.ceau</groupId>
<artifactId>simplemail</artifactId>
<version>0.9.5</version>
```

###	Source code
Source code is available on [GitHub](https://github.com/mdewilde/simplemail).

### Javadoc
Browse this project's [Javadoc](https://www.ceau.be/simplemail/apidocs/index.html).

### Download
Download this project
* [simplemail-0.9.5-javadoc.jar](https://www.ceau.be/simplemail/simplemail-0.9.5-javadoc.jar)
* [simplemail-0.9.5-javadoc.jar.asc](https://www.ceau.be/simplemail/simplemail-0.9.5-javadoc.jar.asc)
* [simplemail-0.9.5-sources.jar](https://www.ceau.be/simplemail/simplemail-0.9.5-sources.jar)
* [simplemail-0.9.5-sources.jar.asc](https://www.ceau.be/simplemail/simplemail-0.9.5-sources.jar.asc)
* [simplemail-0.9.5.jar](https://www.ceau.be/simplemail/simplemail-0.9.5.jar)
* [simplemail-0.9.5.jar.asc](https://www.ceau.be/simplemail/simplemail-0.9.5.jar.asc)
* [simplemail-0.9.5.pom](https://www.ceau.be/simplemail/simplemail-0.9.5.pom)
* [simplemail-0.9.5.pom.asc](https://www.ceau.be/simplemail/simplemail-0.9.5.pom.asc)

### GnuPG public key
Verify signature files with my [GnuPG public key](https://www.ceau.be/pubkey.gpg).

### License
SimpleMail is licensed under [the Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.txt).