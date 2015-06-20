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
package be.ceau.mailer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Straightforward mail service that handles the sending of one-off messages.
 */
public class Mailer {

	private static final Logger logger = LoggerFactory.getLogger(Mailer.class);

	/**
	 * The SMTP host to send emails over.
	 */
	private final String smtpHost;
	
	/**
	 * A collection of bcc recipients that will be added to every email
	 */
	private final Collection<InternetAddress> bccs;
	
	/**
	 * Construct a new Mailer instance to send mail over the given SMTP host.
	 * @param smtp a valid SMTP host
	 * @throws IllegalArgumentException if smtp is blank
	 */
	public Mailer(String smtp) {
		this(smtp, null);
	}

	/**
	 * Construct a new Mailer instance to send mail over the given SMTP host and including the given bccs (if any) in all emails sent.
	 * @param smtp a valid SMTP host
	 * @param bccs a collection of bcc email address that can be parsed to {@link javax.mail.internet.InternetAddress} objects, may be <code>null</code> or empty.
	 * @throws IllegalArgumentException if smtp is blank
	 * @throws IllegalArgumentException if one of the included bcc addresses cannot be parsed as {@link javax.mail.internet.InternetAddress}
	 */
	public Mailer(String smtp, Collection<String> bccs) {
		if (smtp == null) {
			throw new IllegalArgumentException("smtp argument can not be null");
		}
		this.smtpHost = smtp;
		if (bccs == null) {
			this.bccs = Collections.emptyList();
		} else {
			List<InternetAddress> list = new ArrayList<InternetAddress>();
			for (String bcc : bccs) {
				try {
					list.add(new InternetAddress(bcc));
				} catch (AddressException e) {
					throw new IllegalArgumentException(e);
				}
			}
			this.bccs = Collections.unmodifiableList(list);
		}
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
			message.addRecipient(Message.RecipientType.TO, mail.getTo());
			for (InternetAddress cc : mail.getCcs()) {
				message.addRecipient(RecipientType.CC, cc);
			}
			for (InternetAddress bcc : mail.getBccs()) {
				message.addRecipient(RecipientType.BCC, bcc);
			}
			if (bccs != null) {
				for (InternetAddress bcc : bccs) {
					message.addRecipient(RecipientType.BCC, bcc);
				}
			}
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
			logger.debug("send(Mail {}): success", mail);
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
