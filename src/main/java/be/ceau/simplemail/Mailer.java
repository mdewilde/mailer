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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
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
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mailer service that handles the sending of one-off messages.<br>
 * Instances of this class are immutable and threadsafe.<br>
 * To create new instances of this class, a public constructor is available, as
 * well as a fluent builder that provides some optional extras.
 */
public class Mailer {

	private static final Logger logger = LoggerFactory.getLogger(Mailer.class);

	/**
	 * The SMTP host to send emails over.
	 */
	private final String smtpHost;
	
	/**
	 * The InternetAddress to use as 'from' for every email sent by <code>this</code> Mailer instance.
	 */
	private final InternetAddress from;

	/**
	 * List of InternetAddress instances to add as 'to' for every email sent by <code>this</code> Mailer instance.
	 */
	private final List<InternetAddress> tos;

	/**
	 * List of InternetAddress instances to add as 'cc' for every email sent by <code>this</code> Mailer instance.
	 */
	private final List<InternetAddress> ccs;
	
	/**
	 * List of InternetAddress instances to add as 'bcc' for every email sent by <code>this</code> Mailer instance.
	 */
	private final List<InternetAddress> bccs;

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
		this.from = null;
		this.tos = Collections.emptyList();
		this.ccs = Collections.emptyList();
		this.bccs = Collections.emptyList();
	}

	/**
	 * private constructor for Mailer.Builder use only
	 */
	private Mailer(Mailer.Builder builder) {
		this.smtpHost = builder.smtp;
		this.from = builder.from;
		this.tos = Collections.unmodifiableList(new ArrayList<InternetAddress>(builder.tos));
		this.ccs = Collections.unmodifiableList(new ArrayList<InternetAddress>(builder.ccs));
		this.bccs = Collections.unmodifiableList(new ArrayList<InternetAddress>(builder.bccs));
	}

	/**
	 * @return a new Builder object to construct a Mailer instance
	 * @param smtp a valid SMTP host
	 */
	public static Mailer.Builder builder(String smtp) {
		return new Mailer.Builder(smtp);
	}
	
	/**
	 * Verifies and sends given Mail argument.<br>
	 * Verification checks whether sufficient information is availabe for sending the given Mail instance.<br>
	 * In accordance with <a href="https://www.ietf.org/rfc/rfc2822.txt">RFC 2822</a>, the only required 
	 * email headers are origination date (section 3.6.1) and from (section 3.6.2).
	 * This library deviates from this only in that it also requires at least one 'to' address. 
	 * All other fields are optional.<br>
	 * The from address is that which is set in the Mailer instance at construction, unless a value is also specified
	 * in the given Mail argument. In that case, the <strong>value from the Mail instance</strong> is used.
	 * @param mail the Mail instance to send
	 * @return true if the Mail instance was sent
	 * @throws IllegalArgumentException if the Mail argument is <code>null</code> or invalid
	 * @throws IllegalStateException if neither <code>this</code> Mailer nor the Mail argument have a 'from' email address set
	 */
	public boolean send(Mail mail) {
		if (mail == null) {
			throw new IllegalArgumentException("Mail argument can not be null");
		}
		InternetAddress from = determineFrom(mail);
		if (!mail.hasTo()) {
			throw new IllegalArgumentException("Mail argument must have at least one 'to' address");
		}
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", smtpHost);
		Session session = Session.getInstance(properties);
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(from);
			add(Message.RecipientType.TO, this.tos, message);
			add(Message.RecipientType.TO, mail.getTos(), message);
			add(Message.RecipientType.CC, this.ccs, message);
			add(Message.RecipientType.CC, mail.getCcs(), message);
			add(Message.RecipientType.BCC, this.bccs, message);
			add(Message.RecipientType.BCC, mail.getBccs(), message);
			if (mail.hasSubject()) {
				message.setSubject(mail.getSubject(), "UTF-8");
			}
			if (mail.hasTxt() && mail.hasHtml()) {
				Multipart multipart = new MimeMultipart("alternative");
				multipart.addBodyPart(mail.getTxtBodyPart());
				multipart.addBodyPart(mail.getHtmlBodyPart());
				BodyPart wrapper = new MimeBodyPart();
				wrapper.setContent(multipart, "text/alternative");
				Multipart mwrapper = new MimeMultipart("mixed");
				mwrapper.addBodyPart(wrapper);
				message.setContent(mwrapper);
			} else if (mail.hasTxt()) {
				message.setText(mail.getTxt(), "UTF-8", "plain");
			} else if (mail.hasHtml()) {
				message.setText(mail.getHtml(), "UTF-8", "html");
			} else {
				message.setText("", "UTF-8", "plain");
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

	/**
	 * @throws NullPointerException if addresses or message argument is <code>null</code>
	 * @throws MessagingException
	 */
	private void add(Message.RecipientType type, Collection<? extends Address> addresses, MimeMessage message) throws MessagingException {
		for (Address address : addresses) {
			message.addRecipient(type, address);
		}
	}
	
	/**
	 * @return an InternetAddress, never <code>null</code>
	 * @throws IllegalStateException if neither <code>this</code> Mailer nor the Mail argument have a 'from' email address set
	 */
	private InternetAddress determineFrom(Mail mail) {
		if (mail.hasFrom()) {
			if (this.from != null) {
				logger.info("both Mail argument and this Mailer instance have 'from' email address - using " + mail.getFrom());
			}
			return mail.getFrom();
		} else if (this.from != null) {
			return this.from;
		} else {
			throw new IllegalStateException("this Mailer instance has no default 'from' address and the Mail argument does not provide one");
		}
	}
	
	public static class Builder {
		
		private final String smtp;
		private InternetAddress from;
		private final List<InternetAddress> tos = new ArrayList<InternetAddress>();
		private final List<InternetAddress> ccs = new ArrayList<InternetAddress>();
		private final List<InternetAddress> bccs = new ArrayList<InternetAddress>();
		
		private Builder(String smtp) {
			this.smtp = smtp;
		}
		
		/**
		 * Sets an email address that will be used as the from address <strong>for any Mail instance that
		 * does not specify such an address</strong>.
		 * If both the Mailer instance and a Mail message have a from address set, 
		 * <strong>the from set in the Mail instance is used and a message is logged at the info level</strong>.
		 * @param from a valid, non-null {@link javax.mail.internet.InternetAddress}
		 * @return this Mail instance, to allow for method chaining
		 * @throws IllegalArgumentException if argument is <code>null</code>
		 */
		public Mailer.Builder from(InternetAddress from) {
			if (from == null) {
				throw new IllegalArgumentException("from argument can not be null");
			}
			this.from = from;
			return this;
		}

		/**
		 * Sets an email address that will be used as the from address for <strong>to every email sent</strong>
		 * by the resulting Mailer instance. If a Mail instance is sent that has its own from field,
		 * <strong>the field of that Mail instance will not be used and a warning will be logged</strong>.
		 * @param from a valid, non-blank String that can be parsed as {@link javax.mail.internet.InternetAddress}
		 * @return this Mail instance, to allow for method chaining
		 * @throws IllegalArgumentException if argument is not a legal email address
		 */
		public Mailer.Builder from(String from) {
			this.from = convert(from);
			return this;
		}

		/**
		 * Adds an email address to the list of <strong>to</strong> addresses that will be added 
		 * <strong>to every email sent</strong> by the resulting Mailer instance.
		 * @param to a valid, non-null {@link javax.mail.internet.InternetAddress}
		 * @return this Mailer.Builder instance, to allow for method chaining
		 * @throws IllegalArgumentException if argument is <code>null</code>
		 */
		public Mailer.Builder addTo(InternetAddress to) {
			if (to == null) {
				throw new IllegalArgumentException("to argument can not be null");
			}
			this.tos.add(to);
			return this;
		}

		/**
		 * Adds an email address to the list of <strong>to</strong> addresses that will be added 
		 * <strong>to every email sent</strong> by the resulting Mailer instance.
		 * @param to a valid, non-blank String that can be parsed as {@link javax.mail.internet.InternetAddress}
		 * @return this Mailer.Builder instance, to allow for method chaining
		 * @throws IllegalArgumentException if argument is a wrongly formatted address
		 */
		public Mailer.Builder addTo(String to) {
			this.tos.add(convert(to));
			return this;
		}
		
		/**
		 * Adds an email address to the list of <strong>cc</strong> addresses that will be added 
		 * <strong>to every email sent</strong> by the resulting Mailer instance.
		 * @param cc a valid, non-blank String that can be parsed as {@link javax.mail.internet.InternetAddress}
		 * @return this Mailer.Builder instance, to allow for method chaining
		 * @throws IllegalArgumentException if argument is a wrongly formatted address
		 */
		public Mailer.Builder addCc(String cc) {
			this.ccs.add(convert(cc));
			return this;
		}

		/**
		 * Adds an email address to the list of <strong>cc</strong> addresses that will be added 
		 * <strong>to every email sent</strong> by the resulting Mailer instance.
		 * @param cc a valid, non-null {@link javax.mail.internet.InternetAddress}
		 * @return this Mailer.Builder instance, to allow for method chaining
		 * @throws IllegalArgumentException if argument is <code>null</code>
		 */
		public Mailer.Builder addCc(InternetAddress cc) {
			if (cc == null) {
				throw new IllegalArgumentException("cc argument can not be null");
			}
			this.ccs.add(cc);
			return this;
		}

		/**
		 * Adds an email address to the list of <strong>bcc</strong> addresses that will be added 
		 * <strong>to every email sent</strong> by the resulting Mailer instance.
		 * @param bcc a valid, non-blank String that can be parsed as {@link javax.mail.internet.InternetAddress}
		 * @return this Mailer.Builder instance, to allow for method chaining
		 * @throws IllegalArgumentException if argument is a wrongly formatted address
		 */
		public Mailer.Builder addBcc(String bcc) {
			this.bccs.add(convert(bcc));
			return this;
		}

		/**
		 * Adds an email address to the list of <strong>bcc</strong> addresses that will be added 
		 * <strong>to every email sent</strong> by the resulting Mailer instance.
		 * @param bcc a valid, non-null {@link javax.mail.internet.InternetAddress}
		 * @return this Mailer.Builder instance, to allow for method chaining
		 * @throws IllegalArgumentException if argument is <code>null</code>
		 */
		public Mailer.Builder addBcc(InternetAddress bcc) {
			if (bcc == null) {
				throw new IllegalArgumentException("bcc argument can not be null");
			}
			this.bccs.add(bcc);
			return this;
		}

		public Mailer build() {
			return new Mailer(this);
		}
		
	}

	static InternetAddress convert(String email) {
		try {
			return new InternetAddress(email);
		} catch (AddressException e) {
			throw new IllegalArgumentException(email + " is a wrongly formatted address");
		}
	}

}