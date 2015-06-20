# Mailer

Mailer is a simple Java SMTP email library.

### Usage example

Create a Mailer instance that will send email using localhost:

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

Mailer is licensed under [the Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.txt/).