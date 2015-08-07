/*
	Copyright 2015 Marceau Dewilde <m@ceau.be>

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package be.ceau.simplemail;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mailer service that handles the sending of one-off messages.
 */
public class Mailer {

	private static final Logger logger = LoggerFactory.getLogger(Mailer.class);

	/**
	 * The SMTP host to send emails over.
	 */
	private final String smtpHost;
	
	/**
	 * Construct a new Mailer instance to send mail over the given SMTP host.
	 * @param smtp a valid SMTP host
	 * @throws IllegalArgumentException if smtp is blank
	 */
	public Mailer(String smtp) {
		if (smtp == null || smtp.trim().length() == 0) {
			throw new IllegalArgumentException("smtp argument can not be blank");
		}
		this.smtpHost = smtp;
	}

	public boolean send(Mail mail) {
		if (mail == null || !mail.isValid()) {
			throw new IllegalArgumentException("Mail argument is null or not valid");
		}
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", smtpHost);
		Session session = Session.getInstance(properties);
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(mail.getFrom());
			message.addRecipients(Message.RecipientType.TO, mail.getTos().toArray(new Address[mail.getTos().size()]));
			message.addRecipients(Message.RecipientType.CC, mail.getCcs().toArray(new Address[mail.getCcs().size()]));
			message.addRecipients(Message.RecipientType.BCC, mail.getBccs().toArray(new Address[mail.getBccs().size()]));
			message.setSubject(mail.getSubject(), "UTF-8");
			if (mail.getHtml() == null) {
				message.setText(mail.getTxt(), "UTF-8", "plain");
			} else {
				BodyPart txtPart = new MimeBodyPart();
				txtPart.setContent(mail.getTxt(), "text/plain; charset=UTF-8");
				BodyPart htmlPart = new MimeBodyPart();
				htmlPart.setContent(mail.getHtml(), "text/html; charset=UTF-8");
				Multipart multipart = new MimeMultipart("alternative");
				multipart.addBodyPart(txtPart);
				multipart.addBodyPart(htmlPart);
				BodyPart wrapper = new MimeBodyPart();
				wrapper.setContent(multipart, "text/alternative");
				Multipart mwrapper = new MimeMultipart("mixed");
				mwrapper.addBodyPart(wrapper);
				message.setContent(mwrapper);
			}
			Transport.send(message);
			logger.trace("send(Mail {}): success", mail);
			return true;
		} catch (SendFailedException e) {
			logger.error("send(Mail {})", mail, e);
			return false;
		} catch (MessagingException e) {
			logger.error("send(Mail {})", mail, e);
			return false;
		}
	}

}
