# SimpleMail

SimpleMail is (yet another) simple Java SMTP email sender.

### Usage example

Create a SimpleMail instance that will send email using localhost:

```Java
Mailer mailer = new Mailer("localhost");
```

Construct an email:

```Java
Mail mail = new Mail("localhost").from("m@test.com").to("t@test.com").withSubject("Hi").withText("This is your email");
```

Send the email:

```Java
mailer.send(mail);
```

Include this project from Maven Central:

```XML
<groupId>be.ceau</groupId>
<artifactId>simplemail</artifactId>
<version>0.9</version>
```

SimpleMail is licensed under [the Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.txt/).